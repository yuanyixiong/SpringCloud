package online.qsx.demo.eurekaserver02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class Eurekaserver02Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Eurekaserver02Application.class, args);
	}
	
}
