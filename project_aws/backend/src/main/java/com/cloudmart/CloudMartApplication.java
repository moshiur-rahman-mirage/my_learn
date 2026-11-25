package com.cloudmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync        // local order worker runs asynchronously
@EnableScheduling   // used by the in-app SQS poller you add in Phase 7
public class CloudMartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudMartApplication.class, args);
    }
}
