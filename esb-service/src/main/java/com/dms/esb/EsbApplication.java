/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.client.RestTemplate
 */
package com.dms.esb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EsbApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsbApplication.class, (String[])args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
