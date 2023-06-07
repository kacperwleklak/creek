//package pl.poznan.put.kacperwleklak.creek.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.h2.tools.Server;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//import pl.poznan.put.kacperwleklak.cab.CAB;
//import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
//import pl.poznan.put.kacperwleklak.cab.CabPredicate;
//import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
//import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;
//import pl.poznan.put.kacperwleklak.creek.interfaces.CreekClient;
//import pl.poznan.put.kacperwleklak.creek.interfaces.OperationExecutor;
//import pl.poznan.put.kacperwleklak.creek.postgres.PostgresServer;
//import pl.poznan.put.kacperwleklak.creek.protocol.Operation;
//import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
//import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;
//
//import javax.annotation.PostConstruct;
//import java.sql.SQLException;
//import java.util.Map;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//import static pl.poznan.put.kacperwleklak.creek.service.Creek.PREDICATE_ID;
//
////@Service
////@DependsOn({"messageUtils"})
////@Primary
//@Slf4j
//public class LockControlledCreekAdapter implements ReliableChannelDeliverListener, CabDeliverListener, CabPredicate, OperationExecutor {
//
//    private final Creek creek;
//    private final Lock lock;
//    private final ReliableChannel reliableChannel;
//    private final CAB cab;
//    private final Server pgServer;
//
//    @Autowired
//    public LockControlledCreekAdapter(CAB cab,
//                                      ReliableChannel reliableChannel,
//                                      @Value("${postgres.port}") String pgPort,
//                                      @Value("${communication.replicas.id}") int replicaId,
//                                      @Value("${cab.probability}") double cabProbability) throws SQLException {
//        this.cab = cab;
//        this.reliableChannel = reliableChannel;
//
//        PostgresServer postgresServer = new PostgresServer(this);
//        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);
//
//        this.creek = new Creek(cab, reliableChannel, replicaId, cabProbability, postgresServer);
//        this.lock = new ReentrantLock(false);
//    }
//
//    @PostConstruct
//    public void postInitialization() throws SQLException {
//        log.info("LockControlledCreekAdapter initialized");
//        reliableChannel.registerListener(this);
//        cab.registerListener(this);
//        cab.start(Map.of(PREDICATE_ID, this));
//        pgServer.start();
//    }
//
//    @Override
//    public void cabDelver(CabMessageID messageID) {
//        log.debug("async cabDeliver");
//        lock.lock();
//        creek.cabDelver(messageID);
//        lock.unlock();
//    }
//
//    @Override
//    public boolean testSync(CabMessageID messageID) {
//        log.debug("async testSync");
//        lock.lock();
//        boolean result = creek.testSync(messageID);
//        lock.unlock();
//        return result;
//    }
//
//    @Override
//    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
//        log.debug("async testAsync");
//        lock.lock();
//        boolean result = creek.testAsync(messageID, predicateCallback);
//        lock.unlock();
//        return result;
//    }
//
//    @Override
//    public void executeOperation(Operation operation, CreekClient client) {
//        log.debug("async executeOperation");
//        lock.lock();
//        creek.executeOperation(operation, client);
//        lock.unlock();
//    }
//
//    @Override
//    public void rDeliver(byte msgType, byte[] msg) {
//        log.debug("async rDeliver");
//        lock.lock();
//        creek.rDeliver(msgType, msg);
//        lock.unlock();
//    }
//}
