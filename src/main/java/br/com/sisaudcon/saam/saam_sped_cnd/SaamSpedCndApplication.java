package br.com.sisaudcon.saam.saam_sped_cnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SaamSpedCndApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaamSpedCndApplication.class, args);
	}

}
