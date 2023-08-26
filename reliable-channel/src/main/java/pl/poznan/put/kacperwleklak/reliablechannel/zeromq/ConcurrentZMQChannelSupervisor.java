package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class ConcurrentZMQChannelSupervisor implements ReliableChannelDeliverListener {

    private final ThreadPoolExecutor executor;
    private final ReliableChannel reliableChannel;
    private final List<ThriftReliableChannelClient> clients;


    @Autowired
    public ConcurrentZMQChannelSupervisor(ReliableChannel reliableChannel,
                                          @Value("${communication.replicas.nodes}") List<String> replicasAddresses) {
        this.reliableChannel = reliableChannel;
        this.clients = new CopyOnWriteArrayList<>();
        this.executor = new ExceptionHandlingThreadPoolExecutor(2 * replicasAddresses.size());
    }

    @PostConstruct
    public void postConstruct() {
        reliableChannel.registerListener(this);
    }

    public void rCast(TBase msg) {
        executor.execute(() -> serializeAndCast(msg));
    }

    public void registerListener(ThriftReliableChannelClient deliverListener) {
        this.clients.add(deliverListener);
    }

    @Override
    public void rDeliver(byte msgType, byte[] msg) {
        executor.execute(() -> deserializeAndDeliver(msgType, msg));
    }

    private void serializeAndCast(TBase msg) {
        try {
            byte[] serialized = ThriftSerializer.serialize(msg);
            reliableChannel.rCast(serialized);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    private void deserializeAndDeliver(byte msgType, byte[] msg) {
        try {
            byte resolvedMsgType = ThriftSerializer.getMsgType(msg);
            ThriftReliableChannelClient client = clients.stream()
                    .filter(c -> c.canHandle(resolvedMsgType))
                    .findAny()
                    .orElseThrow();
            TBase resolved = client.resolve(resolvedMsgType);
            ThriftSerializer.deserialize(resolved, msg);
            client.rbDeliver(resolved);
        } catch (TException e) {
            throw new RuntimeException(e);
        }

    }
}
