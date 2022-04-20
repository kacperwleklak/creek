package pl.poznan.put.kacperwleklak.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.creek.Creek;
import pl.poznan.put.kacperwleklak.creek.OperationRequest;

@Slf4j
@RestController
public class TestController {

    Creek creek;

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
