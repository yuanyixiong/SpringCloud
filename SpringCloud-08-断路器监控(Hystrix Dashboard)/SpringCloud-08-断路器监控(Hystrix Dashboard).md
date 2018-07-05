# SpringCloud-08-断路器监控(Hystrix Dashboard)

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）
* 同时启动两个client端口号分别为：8762、8763

## 创建服务消费者ribbon
* 参考：SpringCloud-03-服务消费者（rest+ribbon）

## 服务消费者ribbon实现hystrix
* 参考：SpringCloud-07-断路器（Hystrix）

## 服务消费者ribbon实现hystrix dashboard
1. 修改pom添加hystrix dashboard依赖
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
</dependency>
```

2. 修改启动类添加@EnableHystrixDashboard开启仪表盘
```java
package online.qsx.demo.eurekaribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * 服务消费者 rest+ribbon 内置负载均衡
 */
@EnableHystrix
@EnableHystrixDashboard
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
![image](https://note.youdao.com/yws/public/resource/c26693c4b214075fcd1db2c496e6362d/xmlnote/B57A9A14D69F411BAAC47BC699159FE7/9254)

5. 请求：http://localhost:8764/hystrix.stream 查看断路器(hystrix)监控数据
![image](https://note.youdao.com/yws/public/resource/c26693c4b214075fcd1db2c496e6362d/xmlnote/548A99D95E604108B598A56C0A3EBD4E/9250)

6. 请求：http://localhost:8764/hystrix 开启仪表盘
![image](https://note.youdao.com/yws/public/resource/c26693c4b214075fcd1db2c496e6362d/xmlnote/B5A6F662743B4C48B017B224EB45C77A/9252)

7. 填入2000、 http://localhost:8764/hystrix.stream、ribbon 等参数使用仪表盘监控服务断路器状态
![image](https://note.youdao.com/yws/public/resource/c26693c4b214075fcd1db2c496e6362d/xmlnote/276B8E9321E444CE9D8A2491E2674EC5/9251)

8. 单击“Monitor Stream”查看仪表
![image](https://note.youdao.com/yws/public/resource/c26693c4b214075fcd1db2c496e6362d/xmlnote/79668974E0C948E59A5A7C840674BF51/9253)
