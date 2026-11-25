package com.daybook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DaybookApplication {
    public static void main(String[] args) {
        SpringApplication.run(DaybookApplication.class, args);
    }
}
