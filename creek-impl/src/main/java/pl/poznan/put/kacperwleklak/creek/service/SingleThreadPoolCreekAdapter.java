package pl.poznan.put.kacperwleklak.creek.service;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;
import pl.poznan.put.appcommon.concurrent.RejectedExecutionHandlerImpl;
import pl.poznan.put.appcommon.concurrent.RepeatableIdleTaskExecutor;
import pl.poznan.put.kacperwleklak.creek.interfaces.AllOpsDoneListener;
import pl.poznan.put.appcommon.db.OperationExecutor;
import pl.poznan.put.appcommon.db.PostgresServer;
import pl.poznan.put.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;

import static pl.poznan.put.kacperwleklak.creek.service.Creek.PREDICATE_ID;

@Service
@DependsOn({"messageUtils"})
@Primary
@Slf4j
@EnableAsync
public class SingleThreadPoolCreekAdapter implements ReliableChannelDeliverListener, CabDeliverListener, CabPredicate, OperationExecutor, AllOpsDoneListener {

    private final Creek creek;
    private final RepeatableIdleTaskExecutor singleThreadExecutor;
    private final ReliableChannel reliableChannel;
    private final CAB cab;
    private final Server pgServer;

    @Autowired
    public SingleThreadPoolCreekAdapter(CAB cab,
                                        ReliableChannel reliableChannel,
                                        @Value("${postgres.port}") String pgPort,
                                        @Value("${communication.replicas.id}") int replicaId,
                                        @Value("${cab.probability}") double cabProbability) throws SQLException {
        this.cab = cab;
        this.reliableChannel = reliableChannel;

        PostgresServer postgresServer = new PostgresServer(this);
        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);

        this.creek = new Creek(cab, reliableChannel, replicaId, cabProbability, postgresServer, this);

        singleThreadExecutor = new RepeatableIdleTaskExecutor(creek::executeSingleStep);
        singleThreadExecutor.setCorePoolSize(1);
        singleThreadExecutor.setThreadNamePrefix("Creek-");
        singleThreadExecutor.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
        singleThreadExecutor.initialize();
    }

    @PostConstruct
    public void postInitialization() throws SQLException {
        log.info("SingleThreadPoolCreekAdapter initialized");
        reliableChannel.registerListener(this);
        cab.registerListener(this);
        cab.start(Map.of(PREDICATE_ID, this));
        pgServer.start();
    }

    @Override
    public void cabDelver(CabMessageID messageID) {
        log.debug("async cabDeliver");
        singleThreadExecutor.execute(() -> creek.cabDelver(messageID));
    }

    @Override
    public boolean testSync(CabMessageID messageID) {
        log.debug("sync testSync");
        return creek.testSync(messageID);
    }

    @Override
    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
        log.debug("sync testAsync");
        return creek.testAsync(messageID, predicateCallback);
    }

    @Override
    public void executeOperation(pl.poznan.put.appcommon.db.request.Operation operation, ResponseGenerator client) {
        log.debug("async executeOperation");
        singleThreadExecutor.execute(() -> creek.executeOperation(operation, client));
    }

    @Override
    public void rDeliver(byte msgType, byte[] msg) {
        log.debug("async rDeliver");
        singleThreadExecutor.execute(() -> creek.rDeliver(msgType, msg));
    }

    @Override
    public void notifyNothingToDo() {
        log.debug("stopped operations executing");
        singleThreadExecutor.stopRepeating();
    }
}
