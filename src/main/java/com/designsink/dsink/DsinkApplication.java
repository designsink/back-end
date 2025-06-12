package com.designsink.dsink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DsinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsinkApplication.class, args);
	}

}
