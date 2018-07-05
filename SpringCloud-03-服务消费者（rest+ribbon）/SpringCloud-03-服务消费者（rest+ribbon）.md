# SpringCloud-03-服务消费者（rest+ribbon）

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）
* 同时启动两个client端口号分别为：8762、8763

## 创建服务消费者ribbon
1. 首先创建一个maven主工程。
* groupId：online.qsx.demo
* artifactId：eureka-ribbon
* packaging：jar

2. 添加Spring Cloud的pom.xml依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>online.qsx.demo</groupId>
  <artifactId>eureka-ribbon</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  
  	<name>eureka-ribbon</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.13.RELEASE</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Edgware.SR3</spring-cloud.version>
	</properties>

	<dependencies>
		<!--服务注册 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
		<!--ribbon-->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-ribbon</artifactId>
		</dependency>
        <!--spring boot web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring boot 单元测试-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

		<!--JDK版本应模块化缺少得jar -->
		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-impl</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>org.glassfish.jaxb</groupId>
			<artifactId>jaxb-runtime</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
			<groupId>javax.activation</groupId>
			<artifactId>activation</artifactId>
			<version>1.1.1</version>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<finalName>eureka-ribbon</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
	
</project>
```

3. 配置application.yml属性文件src/main/resource/application.yml
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8764
spring:
  application:
    name: eureka-ribbon
```

4. 新建启动类EurekaribbonApplication.java使用RestTemplate API模拟请求实现服务的消费
```java
package online.qsx.demo.eurekaribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 服务消费者 rest+ribbon 内置负载均衡
 */
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
	public String hi01() {
		return restTemplate.getForObject("http://eureka-client/hi?name=arvin01", String.class);
	}
}
```

5. run as 依次启动 服务注册中心(eureka server)、多个服务提供者(eureka client)、服务消费者(eureka ribbon)

6. 访问：http://localhost:8761
![image](https://note.youdao.com/yws/public/resource/2bf5d32bf66dce8017ed81ad631f12da/xmlnote/B10171618F9A4E86818C227D94949DF1/8827)

7. 访问：http://localhost:8764/hi01
![image](https://note.youdao.com/yws/public/resource/2bf5d32bf66dce8017ed81ad631f12da/xmlnote/8FAFD56C4F2241B6B161DF334CC39140/8829)
![image](https://note.youdao.com/yws/public/resource/2bf5d32bf66dce8017ed81ad631f12da/xmlnote/25BDAE9315B54BFFA6F47289710D3AAD/8831)
