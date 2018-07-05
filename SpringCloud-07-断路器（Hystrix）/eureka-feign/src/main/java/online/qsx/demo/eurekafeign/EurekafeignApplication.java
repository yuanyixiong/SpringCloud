package online.qsx.demo.eurekafeign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@EnableHystrix
@EnableFeignClients
@RestController
@SpringBootApplication
public class EurekafeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekafeignApplication.class, args);
	}

	@Autowired
	private ServiceFeign01 serviceFeign01;

	// 负载均衡
	@GetMapping("/hi01")
	public String hi01() {
		return serviceFeign01.hi("arvin01");
	}
}

// 熔断处理
@FeignClient(value = "eureka-client", fallback = ServiceFeign01Hystric.class)
interface ServiceFeign01 {

	@GetMapping(value = "/hi")
	String hi(@RequestParam(value = "name") String name);
}

// 熔断方法实现
@Component
class ServiceFeign01Hystric implements ServiceFeign01 {
	@Override
	public String hi(String name) {
		return "ServiceFeign01Hystric";
	}
}