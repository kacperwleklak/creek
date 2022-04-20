package pl.poznan.put.kacperwleklak.creek;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import pl.poznan.put.kacperwleklak.operation.CreekOperation;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = OperationRequest.class)
@JsonSubTypes({
        @JsonSubTypes.Type(StrongOperationRequest.class)
})
public class OperationRequest {

    private String uuid;
    private CreekOperation operation;

}
