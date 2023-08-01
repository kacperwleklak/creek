package pl.poznan.put.kacperwleklak.redblue.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import pl.poznan.put.appcommon.db.OperationExecutor;
import pl.poznan.put.appcommon.db.PostgresServer;
import pl.poznan.put.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.redblue.concurrent.PriorityCallable;
import pl.poznan.put.kacperwleklak.redblue.concurrent.PrioritySingleThreadedPoolExecutor;
import pl.poznan.put.kacperwleklak.redblue.interfaces.RedBlueNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.interfaces.TokenNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.protocol.PassToken;
import pl.poznan.put.kacperwleklak.redblue.protocol.Request;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@DependsOn({"messageUtils"})
@Primary
@Slf4j
@EnableAsync
public class SingleThreadPoolRedBlueSupervisor implements ReliableChannelDeliverListener, OperationExecutor,
                                                          RedBlueNotificationReceiver, TokenNotificationReceiver {

    private final RedBlue redBlue;
    private final ExecutorService executor;
    private final ReliableChannel reliableChannel;
    private final Server pgServer;
    private final TokenKeeper tokenKeeper;
    private final int REPLICA_ID;

    @Autowired
    public SingleThreadPoolRedBlueSupervisor(ReliableChannel reliableChannel,
                                             @Value("${postgres.port}") String pgPort,
                                             @Value("${communication.replicas.id}") int replicaId,
                                             @Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                                             @Value("${redblue.token.timetolive}") long tokenTTL) throws SQLException {
        this.reliableChannel = reliableChannel;
        PostgresServer postgresServer = new PostgresServer(this);
        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);
        this.redBlue = new RedBlue(reliableChannel, replicaId, postgresServer, replicasAddresses, this);
        this.tokenKeeper = new TokenKeeper(this, tokenTTL);
        this.REPLICA_ID = replicaId;

        executor = new PrioritySingleThreadedPoolExecutor();
    }

    @PostConstruct
    public void postInitialization() throws SQLException {
        log.info("SingleThreadPoolRedBlueAdapter initialized");
        reliableChannel.registerListener(this);
        pgServer.start();
    }

    @Override
    public void executeOperation(pl.poznan.put.appcommon.db.request.Operation operation, ResponseGenerator client) {
        log.debug("async executeOperation");
        executor.submit(new PriorityCallable(6, () -> redBlue.executeOperation(operation, client)));
    }

    @Override
    public void rDeliver(byte msgType, byte[] msg) {
        log.debug("SingleThreadPoolRedBlueSupervisor rDeliver");
        try {
            if (msgType == (byte) 2) {
                Request request = new Request();
                ThriftSerializer.deserialize(request, msg);
                log.debug("RedBlue handling request {}", request);
                executor.submit(new PriorityCallable(5, () -> redBlue.operationRequestHandler(request)));
            }
            if (msgType == (byte) 1) {
                PassToken passToken = new PassToken();
                ThriftSerializer.deserialize(passToken, msg);
                if ((byte) REPLICA_ID != passToken.getRecipient()) return;
                log.debug("RedBlue handling passToken {}", passToken);
                tokenKeeper.countdownTokenTime();
                executor.submit(new PriorityCallable(2, () -> redBlue.passTokenHandler(passToken)));
            }
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pendingRequestsFlagIsTrue() {
        log.debug("pendingRequestsFlagIsTrue");
        executor.submit(new PriorityCallable(3, redBlue::whenIsPendingRequestFlag));
    }

    @Override
    public void hasTokenAndPendingOwnRedOps() {
        log.debug("pendingRequestsFlagIsTrue");
        executor.submit(new PriorityCallable(4, redBlue::whenIsTokenRedNumberAndPendingOwnRequests));
    }

    @Override
    public void tokenTimeIsUp() {
        log.debug("Token time ended at {}", System.currentTimeMillis());
        executor.submit(new PriorityCallable(1, redBlue::tokenTimeIsUp));
    }
}
