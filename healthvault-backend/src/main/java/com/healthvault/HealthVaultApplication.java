package com.healthvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HealthVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthVaultApplication.class, args);
    }

}
