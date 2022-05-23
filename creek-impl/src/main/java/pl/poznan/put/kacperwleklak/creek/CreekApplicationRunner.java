package pl.poznan.put.kacperwleklak.creek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"pl.poznan.put.kacperwleklak.*"})
public class CreekApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(CreekApplicationRunner.class, args);
    }

}
