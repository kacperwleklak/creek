package pl.poznan.put.kacperwleklak.creek.controller;

import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.kacperwleklak.creek.service.Creek;
import pl.poznan.put.kacperwleklak.creek.service.StateObject;
import pl.poznan.put.kacperwleklak.creek.structure.Request;
import pl.poznan.put.kacperwleklak.creek.structure.Response;

import java.util.Map;

@Slf4j
@RestController
public class TestController {

    private final Creek creek;

    @Autowired
    public TestController(Creek creek) {
        this.creek = creek;
    }

    @PostMapping("/")
    public ResponseEntity<String> test(@RequestBody RequestDTO requestDTO) {
        log.info("Received operationRequest={}", requestDTO);
        if (!isValid(requestDTO)) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        creek.invoke(requestDTO.getOperation(), requestDTO.isStrong());
        return ResponseEntity.ok("\uD83D\uDC4D");
    }

    @GetMapping("/state")
    public ResponseEntity<StateObject> getStateObject() {
        return ResponseEntity.ok(creek.getStateObject());
    }

    @GetMapping("/responses")
    public ResponseEntity<Map<Request, Response>> getResponses() {
        return ResponseEntity.ok(creek.getResponsesMap());
    }

    private boolean isValid(RequestDTO requestDTO) {
        try {
            MVEL.compileExpression(requestDTO.getOperation());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
