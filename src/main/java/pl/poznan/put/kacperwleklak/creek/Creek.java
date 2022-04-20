package pl.poznan.put.kacperwleklak.creek;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.structure.IncrementalIndexList;

@Component
@Slf4j
public class Creek {

    private final IncrementalIndexList<OperationRequest> receivedMessages;

    private final CAB cab;

    @Autowired
    public Creek(@Lazy CAB cab) {
        this.receivedMessages = new IncrementalIndexList<>();
        this.cab = cab;
    }

    public void handleOperationRequest(OperationRequest operationRequest) {
        if (operationRequest instanceof StrongOperationRequest) {
            StrongOperationRequest strongOperationRequest = (StrongOperationRequest) operationRequest;
            cab.cabCast(strongOperationRequest.getUuid(), strongOperationRequest.getPredicate());
        } else {
            receivedMessages.add(operationRequest);
        }
    }

    public void cabDeliver(String messageID) {
        log.info("CAB Delivered: {}", messageID);
    }
}
