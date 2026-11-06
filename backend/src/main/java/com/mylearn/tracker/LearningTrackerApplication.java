package com.mylearn.tracker;

import com.mylearn.tracker.desktop.DesktopLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // automatic backups
public class LearningTrackerApplication {

    public static void main(String[] args) {
        // -Dapp.desktop=true is set by the Windows installer build (scripts/build-installer.ps1)
        if (Boolean.getBoolean("app.desktop")) {
            DesktopLauncher.launch(args);
            return;
        }
        SpringApplication.run(LearningTrackerApplication.class, args);
    }
}
