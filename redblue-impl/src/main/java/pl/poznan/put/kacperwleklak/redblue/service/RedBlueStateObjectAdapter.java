package pl.poznan.put.kacperwleklak.redblue.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.kacperwleklak.appcommon.db.PostgresServer;
import pl.poznan.put.kacperwleklak.appcommon.db.response.Response;
import pl.poznan.put.kacperwleklak.appcommon.db.response.ResponseMessageStream;
import pl.poznan.put.kacperwleklak.appcommon.state.NonVersionedStateObjectSql;
import pl.poznan.put.kacperwleklak.redblue.protocol.Action;
import pl.poznan.put.kacperwleklak.redblue.protocol.Operation;
import pl.poznan.put.kacperwleklak.redblue.protocol.Request;
import pl.poznan.put.kacperwleklak.redblue.state.GeneratorOpResult;
import pl.poznan.put.kacperwleklak.redblue.utils.AppCommonConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class RedBlueStateObjectAdapter extends NonVersionedStateObjectSql {

    private static final int DATA_ROW_TYPE = 'D';
    private static final String ERROR_TYPE_RESPONSE = "ERROR";

    public RedBlueStateObjectAdapter(PostgresServer pgServer) {
        super(pgServer);
    }

    public GeneratorOpResult executeGenerator(Operation operation) {
        Request request = new Request();
        request.setShadowOp(operation);
        if (isReadOnlyStatement(operation)) {
            Response response = super.execute(AppCommonConverter.toAppCommonRequest(request));
            return new GeneratorOpResult(response, null);
        } else if (isGeneratorStatement(operation)) {
            Response response = super.execute(AppCommonConverter.toAppCommonRequest(request));
            String generatedOperation = response.getResponseMessageList()
                    .stream()
                    .filter(responseMessageStream -> DATA_ROW_TYPE == responseMessageStream.getMessageType())
                    .findFirst()
                    .map(this::responseToString)
                    .map(this::extractOperation)
                    .orElse(null);
            return new GeneratorOpResult(response, generatedOperation == null ? null : new Operation(generatedOperation, Action.QUERY));
        } else {
            return new GeneratorOpResult(null, request.getShadowOp());
        }
    }

    public Response executeShadow(Request request) {
        return super.execute(AppCommonConverter.toAppCommonRequest(request));
    }

    private boolean isReadOnlyStatement(Operation operation) {
        return StringUtils.startsWithIgnoreCase(operation.getSql(), "select");
    }

    private boolean isGeneratorStatement(Operation operation) {
        return StringUtils.startsWithIgnoreCase(operation.getSql(), "call");
    }

    private String responseToString(ResponseMessageStream responseMessageStream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            responseMessageStream.writeTo(baos);
            return baos.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractOperation(String fullOperationResponse) {
        log.debug("extracting operation from {}", fullOperationResponse);
        String[] split = fullOperationResponse.split("~~~"); //leaves [0] for some protocol trash
        if (Objects.equals(split[2], ERROR_TYPE_RESPONSE) || Objects.equals(split[1], "")) {
            return null;
        } else return split[1];
    }

}
