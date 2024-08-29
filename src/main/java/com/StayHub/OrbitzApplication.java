package com.StayHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@SpringBootApplication
//@EnableScheduling
@EnableTransactionManagement
public class OrbitzApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrbitzApplication.class, args);
	}

}
