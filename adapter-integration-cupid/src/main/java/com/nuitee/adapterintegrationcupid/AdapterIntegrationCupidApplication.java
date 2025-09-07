package com.nuitee.adapterintegrationcupid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.nuitee")
@EnableFeignClients(basePackages = "com.nuitee.adapterintegrationcupid.client")
public class AdapterIntegrationCupidApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdapterIntegrationCupidApplication.class, args);
    }
}
