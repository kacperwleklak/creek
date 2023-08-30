package pl.poznan.put.kacperwleklak.redblue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"pl.poznan.put.kacperwleklak.*"})
public class RedBlueApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(RedBlueApplicationRunner.class, args);
    }

}
