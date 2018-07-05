# SpringCloud-07-断路器（Hystrix）

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）
* 同时启动两个client端口号分别为：8762、8763

## 创建服务消费者ribbon
* 参考：SpringCloud-03-服务消费者（rest+ribbon）

## 创建服务消费者feign
* 参考：SpringCloud-04-服务消费者（Feign）

## 服务消费者ribbon实现hystrix
* 修改：SpringCloud-03-服务消费者（rest+ribbon）
1. 修改pom添加hystrix依赖
```xml
<!--hystrix 服务监控,数据发送 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
<!-- hystrix 断路器 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

2. 修改rest+ribbon服务调度,添加断路处理(hystrix)
```java
package online.qsx.demo.eurekaribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * 服务消费者 rest+ribbon 内置负载均衡
 */
@EnableHystrix
@RestController
@SpringBootApplication
public class EurekaribbonApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(EurekaribbonApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private RestTemplate restTemplate;

	// 负载均衡 eureka-client
	// 验证负载均衡需要同时启动多个 eureka-client 服务
	@GetMapping("/hi01")
    @HystrixCommand(fallbackMethod = "hiError")
	public String hi01() {
		return restTemplate.getForObject("http://eureka-client/hi?name=arvin01", String.class);
	}
	
    //熔断方法
    public String hiError() {
        return "EurekaribbonApplication hiError";
    }
}
```

3. run as 依次启动 服务注册中心(eureka server)、服务提供者(eureka client)、服务消费者(eureka ribbon)

4. 请求：http://localhost:8764/hi01 完成一次请求调度
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/76E6FE0FCA4A4049AB4B9C50C552AC91/9154)

5. 请求：http://localhost:8764/hystrix.stream 查看断路器(hystrix)监控数据
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/938C42897DE64A7588865B72FC3BBDC6/9150)

6.停止服务提供者(eureka client)会发现服务消费者(eureka ribbon)调用服务提供者(eureka client)的服务失败，使用自定义的处理方案
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/847DA4AD5BAC4397BF915F680DC589B3/9152)

## 服务消费者feign实现hystrix
* 修改：SpringCloud-04-服务消费者（Feign）
1. 修改pom添加hystrix依赖
```xml
<!--hystrix 服务监控,数据发送 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
<!-- hystrix 断路器 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

2. 修改Feign服务调度,添加断路处理(hystrix)
```java
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
```

3. 启动断路监控
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8765
spring:
  application:
    name: eureka-feign
feign:
  hystrix:
    enabled: true
```

4. run as 依次启动 服务注册中心(eureka server)、服务提供者(eureka client)、服务消费者(eureka feign)

5. 请求：http://localhost:8764/hi01 完成一次请求调度
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/964CFBD7FDFA44D78B18EFA331A71690/9160)

6. 请求：http://localhost:8764/hystrix.stream 查看断路器(hystrix)监控数据
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/FB06286D389C4CE6B5952E3F720206EB/9156)

7. 停止服务提供者(eureka client)会发现服务消费者(eureka feign)调用服务提供者(eureka client)的服务失败，使用自定义的处理方案
![image](https://note.youdao.com/yws/public/resource/3ea57ad88bd964a6a5132f6928a639ee/xmlnote/F25D32D37B3A4E2584D17B9FA118C66F/9158)