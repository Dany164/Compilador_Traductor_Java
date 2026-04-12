package com.compilador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TraductorApplication {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SpringApplication.run(TraductorApplication.class, args);
    }
}