package pl.poznan.put.kacperwleklak.redblue.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.poznan.put.kacperwleklak.redblue.interfaces.TokenNotificationReceiver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenKeeper {

    private ScheduledExecutorService service;
    private TokenNotificationReceiver tokenNotificationReceiver;
    private long tokenTTL;


    @Autowired
    public TokenKeeper(TokenNotificationReceiver tokenNotificationReceiver, long tokenTTL) {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.tokenNotificationReceiver = tokenNotificationReceiver;
        this.tokenTTL = tokenTTL;
    }

    public void countdownTokenTime() {
        log.debug("Scheduling token TTL {} ms", tokenTTL);
        Runnable task = () -> tokenNotificationReceiver.tokenTimeIsUp();
        service.schedule(task, tokenTTL, TimeUnit.MILLISECONDS);
    }

}
