package pl.poznan.put.kacperwleklak.redblue.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.appcommon.db.OperationExecutor;
import pl.poznan.put.kacperwleklak.appcommon.db.PostgresServer;
import pl.poznan.put.kacperwleklak.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.appcommon.db.request.Operation;
import pl.poznan.put.kacperwleklak.redblue.concurrent.PriorityCallable;
import pl.poznan.put.kacperwleklak.redblue.concurrent.PrioritySingleThreadedPoolExecutor;
import pl.poznan.put.kacperwleklak.redblue.interfaces.RedBlueNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.interfaces.TokenNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.protocol.PassToken;
import pl.poznan.put.kacperwleklak.redblue.protocol.Request;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ConcurrentZMQChannelSupervisor;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ThriftReliableChannelClient;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@DependsOn({"messageUtils"})
@Primary
@Slf4j
@EnableAsync
public class SingleThreadPoolRedBlueSupervisor implements ThriftReliableChannelClient, OperationExecutor,
                                                          RedBlueNotificationReceiver, TokenNotificationReceiver {

    private final RedBlue redBlue;
    private final ExecutorService executor;
    private final ConcurrentZMQChannelSupervisor thriftZmqChannel;
    private final Server pgServer;
    private final TokenKeeper tokenKeeper;
    private final int REPLICA_ID;

    @Autowired
    public SingleThreadPoolRedBlueSupervisor(ConcurrentZMQChannelSupervisor thriftZmqChannel,
                                             @Value("${postgres.port}") String pgPort,
                                             @Value("${communication.replicas.id}") int replicaId,
                                             @Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                                             @Value("${redblue.token.timetolive}") long tokenTTL) throws SQLException {
        this.thriftZmqChannel = thriftZmqChannel;
        PostgresServer postgresServer = new PostgresServer(this);
        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);
        this.redBlue = new RedBlue(thriftZmqChannel, replicaId, postgresServer, replicasAddresses, this);
        this.tokenKeeper = new TokenKeeper(this, tokenTTL);
        this.REPLICA_ID = replicaId;

        executor = new PrioritySingleThreadedPoolExecutor();
    }

    @PostConstruct
    public void postInitialization() throws SQLException {
        log.info("SingleThreadPoolRedBlueAdapter initialized");
        thriftZmqChannel.registerListener(this);
        pgServer.start();
    }

    @Override
    public void executeOperation(Operation operation, ResponseGenerator client) {
        log.debug("async executeOperation");
        executor.submit(new PriorityCallable(5, () -> redBlue.executeOperation(operation, client)));
    }

    @Override
    public void rbDeliver(TBase msg) {
        log.debug("SingleThreadPoolRedBlueSupervisor rDeliver");
        if (msg instanceof Request) {
            Request request = (Request) msg;
            executor.submit(new PriorityCallable(4, request.getRequestID().getCurrEventNo(), () -> redBlue.operationRequestHandler(request)));
        }
        if (msg instanceof PassToken) {
            PassToken passToken = (PassToken) msg;
            if ((byte) REPLICA_ID != passToken.getRecipient()) return;
            tokenKeeper.countdownTokenTime();
            executor.submit(new PriorityCallable(2, () -> redBlue.passTokenHandler(passToken)));
        }
    }

    @Override
    public void hasTokenAndPendingOwnRedOps() {
        log.debug("pendingRequestsFlagIsTrue");
        executor.submit(new PriorityCallable(3, redBlue::whenIsTokenRedNumberAndPendingOwnRequests));
    }

    @Override
    public void tokenTimeIsUp() {
        log.debug("Token time ended at {}", System.currentTimeMillis());
        executor.submit(new PriorityCallable(1, redBlue::tokenTimeIsUp));
    }


    @Override
    public TBase resolve(byte msgType) {
        switch (msgType) {
            case 1: return new PassToken();
            case 2: return new Request();
            default: return null;
        }
    }

    @Override
    public boolean canHandle(byte msgType) {
        return msgType == 1 || msgType == 2;
    }
}
