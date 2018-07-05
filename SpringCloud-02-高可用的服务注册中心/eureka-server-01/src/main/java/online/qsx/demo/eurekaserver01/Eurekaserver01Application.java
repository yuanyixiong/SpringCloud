package online.qsx.demo.eurekaserver01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class Eurekaserver01Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Eurekaserver01Application.class, args);
	}
	
}
