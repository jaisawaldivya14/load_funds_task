package com.vault.load;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class LoadFundsTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadFundsTaskApplication.class, args);
	}

}
