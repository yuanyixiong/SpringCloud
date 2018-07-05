package online.qsx.demo.eurekaclientservicehi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableEurekaClient
@SpringBootApplication
public class EurekaclientservicehiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaclientservicehiApplication.class);
	}

	@RequestMapping("/hi")
	public String home(@RequestParam String name) {
		return "hi " + name;
	}
}
