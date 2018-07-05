# SpringCloud-11-消息总线(Spring Cloud Bus)

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建分布式配置中心config
* 参考：SpringCloud-10-高可用的分布式配置中心(Config)

## 使用消息总线(Spring Cloud Bus)实现服务提供者client的配置的在线更新
1. 修改pom.xml添加依赖
```xml
<!-- bus消息总线-->
<dependency>
	<groupId>org.springframework.retry</groupId>
	<artifactId>spring-retry</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

2. 安装rabbitMQ并启动

3. 修改application.yml添加MQ相关配置
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8762
spring:
  application:
    name: eureka-client
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
management:
  security:
    enabled: false
```

4. 修改启动类EurekaclientApplication.java，使用@RefreshScope实现配置更新
```java
package online.qsx.demo.eurekaclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope //http://localhost:8762/bus/refresh
@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaclientApplication.class, args);
	}

	// 读取yml中的配置
	@Value("${server.port}")
	String port;

	// 读取git中的配置
	@Value("${version}")
	String version;

	@RequestMapping("/hi")
	public String home(@RequestParam String name) {
		return "hi " + name + ",i am from port:" + port + " ,version: " + version;
	}
}
```

5.  run as 依次启动 服务注册中心(eureka server)、配置中心(eureka config)、服务提供者(eureka client)

6.  访问git查看配置信息
![image](https://note.youdao.com/yws/public/resource/958e2804269476330b749d2dd3e6fbc4/xmlnote/D3FB353E6AAC478AA0B9005CB94A0632/9437)

7.  访问：http://localhost:8762/hi?name=arvin查看git配置的加载
![image](https://note.youdao.com/yws/public/resource/958e2804269476330b749d2dd3e6fbc4/xmlnote/4362BCC4C8AE408BA41E0591926F1130/9439)

8.  修改git中的配置信息
![image](https://note.youdao.com/yws/public/resource/958e2804269476330b749d2dd3e6fbc4/xmlnote/E036F9873A9544B0A7F95A3E602E48F9/9442)

9.  使用Spring Cloud Bus功能发送POST类型的请求：http://localhost:8762/bus/refresh更新项目配置
![image](https://note.youdao.com/yws/public/resource/958e2804269476330b749d2dd3e6fbc4/xmlnote/E42329FF02A9489A9FBAC9CA50C5C34F/9444)

10.  访问：http://localhost:8762/hi?name=arvin查看更新后的git配置加载
![image](https://note.youdao.com/yws/public/resource/958e2804269476330b749d2dd3e6fbc4/xmlnote/8DCBFC85303147DD97DA61E6525A48DC/9446)