package com.quickbite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for QuickBite Food Ordering App
 * 
 * This Spring Boot application provides a web-based GUI for food ordering
 * using Vaadin framework. The application runs on http://localhost:8080
 */
@SpringBootApplication
public class QuickBiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickBiteApplication.class, args);
    }
}
