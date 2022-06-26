package pl.poznan.put.kacperwleklak.creek.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.kacperwleklak.creek.service.Creek;
import pl.poznan.put.kacperwleklak.creek.service.StateObject;

@Slf4j
@RestController
public class TestController {

    private final Creek creek;

    @Autowired
    public TestController(Creek creek) {
        this.creek = creek;
    }

    @GetMapping("/state")
    public ResponseEntity<StateObject> getStateObject() {
        return ResponseEntity.ok(creek.getStateObject());
    }
}
