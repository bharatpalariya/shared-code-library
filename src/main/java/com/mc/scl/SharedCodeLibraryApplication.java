package com.mc.scl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SharedCodeLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharedCodeLibraryApplication.class, args);
	}

}
