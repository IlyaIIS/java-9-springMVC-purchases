package com.example.purchases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan("com.example")
public class PurchasesApplication {
	public static void main(String[] args) {
		SpringApplication.run(PurchasesApplication.class, args);
	}
}
