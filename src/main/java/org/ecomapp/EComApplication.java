package org.ecomapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.ecomapp")
public class EComApplication {

    public static void main(String[] args) {
        SpringApplication.run(EComApplication.class, args);
    }

}
