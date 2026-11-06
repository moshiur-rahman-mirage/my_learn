package com.mylearn.tracker;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates the directory configured by {@code app.data-dir} before the DataSource is built.
 * Registered in META-INF/spring.factories so it also runs for @SpringBootTest. It fires on the
 * application-prepared event: the environment is final (incl. test overrides) but no bean,
 * including the Hikari DataSource, has been created yet.
 */
public class DataDirectoryInitializer implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        String dir = event.getApplicationContext().getEnvironment().getProperty("app.data-dir", "./data");
        try {
            Files.createDirectories(Path.of(dir));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create data directory: " + dir, e);
        }
    }
}
