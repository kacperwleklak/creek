package pl.poznan.put.kacperwleklak.creek.controller;

import pl.poznan.put.kacperwleklak.creek.services.Creek;
import pl.poznan.put.kacperwleklak.creek.requests.OperationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    private Creek creek;

    @Autowired
    public TestController(Creek creek) {
        this.creek = creek;
    }

    @PostMapping("/")
    public ResponseEntity<String> test(@RequestBody OperationRequest operationRequest) {
        log.info("Received operationRequest={}", operationRequest);
        creek.handleOperationRequest(operationRequest);
        return ResponseEntity.ok("\uD83D\uDC4D");
    }
}
