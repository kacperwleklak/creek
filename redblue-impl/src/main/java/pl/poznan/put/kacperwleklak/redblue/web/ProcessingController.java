package pl.poznan.put.kacperwleklak.redblue.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.kacperwleklak.redblue.interfaces.TokenNotificationReceiver;

@RestController
@Slf4j
public class ProcessingController {

    private final TokenNotificationReceiver tokenNotificationReceiver;

    @Autowired
    public ProcessingController(TokenNotificationReceiver tokenNotificationReceiver) {
        this.tokenNotificationReceiver = tokenNotificationReceiver;
    }

    @GetMapping("/insertToken")
    public void insertToken() {
        log.info("GET /insertToken -> inserted token");
        tokenNotificationReceiver.tokenTimeIsUp();
    }

}
