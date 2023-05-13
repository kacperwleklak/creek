package pl.poznan.put.kacperwleklak.creek.service;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;
import pl.poznan.put.kacperwleklak.creek.interfaces.CreekClient;
import pl.poznan.put.kacperwleklak.creek.interfaces.OperationExecutor;
import pl.poznan.put.kacperwleklak.creek.postgres.PostgresServer;
import pl.poznan.put.kacperwleklak.creek.protocol.Operation;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Map;

import static pl.poznan.put.kacperwleklak.creek.config.AsyncConfigurer.SINGLE_THREAD_EXECUTOR;
import static pl.poznan.put.kacperwleklak.creek.service.Creek.PREDICATE_ID;

@Service
@Slf4j
@DependsOn({"messageUtils"})
@Primary
public class AsyncCreekAdapter implements ReliableChannelDeliverListener, CabDeliverListener, CabPredicate, OperationExecutor {

    private Creek creek;
    private ThreadPoolTaskExecutor singleThreadExecutor;
    private ReliableChannel reliableChannel;
    private CAB cab;
    private final Server pgServer;

    @Autowired
    public AsyncCreekAdapter(@Qualifier(SINGLE_THREAD_EXECUTOR) ThreadPoolTaskExecutor singleThreadExecutor,
                             CAB cab,
                             ReliableChannel reliableChannel,
                             @Value("${postgres.port}") String pgPort,
                             @Value("${communication.replicas.id}") int replicaId,
                             @Value("${cab.probability}") double cabProbability) throws SQLException {
        this.singleThreadExecutor = singleThreadExecutor;
        this.cab = cab;
        this.reliableChannel = reliableChannel;

        PostgresServer postgresServer = new PostgresServer(this);
        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);

        this.creek = new Creek(cab, reliableChannel, replicaId, cabProbability, postgresServer);
    }

    @PostConstruct
    public void postInitialization() throws SQLException {
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
        log.debug("async testSync");
        return singleThreadExecutor.submitListenable(() -> creek.testSync(messageID))
                .completable()
                .join();
    }

    @Override
    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
        log.debug("async testAsync");
        return singleThreadExecutor.submitListenable(() -> creek.testAsync(messageID, predicateCallback))
                .completable()
                .join();
    }

    @Override
    public void executeOperation(Operation operation, CreekClient client) {
        log.debug("async executeOperation");
        singleThreadExecutor.execute(() -> creek.executeOperation(operation, client));
    }

    @Override
    public void rDeliver(byte msgType, byte[] msg) {
        log.debug("async rDeliver");
        singleThreadExecutor.execute(() -> creek.rDeliver(msgType, msg));
    }
}
