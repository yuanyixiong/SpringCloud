package online.qsx.demo.eurekafeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableFeignClients
@RestController
@SpringBootApplication
public class EurekafeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekafeignApplication.class, args);
	}

	@Bean
	public AlwaysSampler defaultSampler(){
		return new AlwaysSampler();
	}
	
	@Autowired
	private ServiceFeign01 serviceFeign01;

	// ¸ºÔØ¾ùºâ
	@GetMapping("/hi01")
	public String hi01() {
		return serviceFeign01.hi("arvin01");
	}
}

@FeignClient(value = "eureka-client")
interface ServiceFeign01 {

	@GetMapping(value = "/hi")
	String hi(@RequestParam(value = "name") String name);
}