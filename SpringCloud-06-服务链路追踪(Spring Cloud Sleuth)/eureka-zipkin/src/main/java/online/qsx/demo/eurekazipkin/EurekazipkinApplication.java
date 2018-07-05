package online.qsx.demo.eurekazipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zipkin.server.EnableZipkinServer;

@EnableZipkinServer
@SpringBootApplication
public class EurekazipkinApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekazipkinApplication.class, args);
	}
}
