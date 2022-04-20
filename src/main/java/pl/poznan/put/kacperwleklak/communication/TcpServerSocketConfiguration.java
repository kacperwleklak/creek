package pl.poznan.put.kacperwleklak.communication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@EnableIntegration
@IntegrationComponentScan
public class TcpServerSocketConfiguration {

    private final int port;

    public TcpServerSocketConfiguration(@Value("${communication.replicas.port}") int port) {
        this.port = port;
    }

    @Bean
    public AbstractServerConnectionFactory tcpServer() {
        log.info("Starting TCP server with port: {}", port);
        TcpNetServerConnectionFactory serverCf = new TcpNetServerConnectionFactory(port);
        serverCf.setSerializer(new ByteArrayCrLfSerializer());
        serverCf.setDeserializer(new ByteArrayCrLfSerializer());
        serverCf.setSoTcpNoDelay(true);
        serverCf.setSoKeepAlive(true);
        return serverCf;
    }

    @Bean
    public MessageChannel fromTcp() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel toTcp() {
        return new DirectChannel();
    }

    @Bean
    public TcpInboundGateway tcpInGate() {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(tcpServer());
        inGate.setRequestChannel(fromTcp());
        inGate.setReplyChannel(toTcp());
        return inGate;
    }


}
