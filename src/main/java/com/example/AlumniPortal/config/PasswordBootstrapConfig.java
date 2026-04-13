package com.example.AlumniPortal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordBootstrapConfig {

    @Bean
    CommandLineRunner generateAdminPassword(PasswordEncoder encoder) {
        return args -> {
            String raw = "Admin1234@";
            String hash = encoder.encode(raw);
            System.out.println("=================================");
            System.out.println("RAW  : " + raw);
            System.out.println("HASH : " + hash);
            System.out.println("=================================");
        };
    }
}
