# SpringCloud-06-服务链路追踪(Spring Cloud Sleuth)

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）
* 同时启动两个client端口号分别为：8762、8763

## 创建服务消费者ribbon
* 参考：SpringCloud-03-服务消费者（rest+ribbon）

## 创建服务消费者feign
* 参考：SpringCloud-04-服务消费者（Feign）

## 创建路由网关服务zuul
* 参考：SpringCloud-05-路由网关(zuul)

## 修改服务添加链路追踪zipkin
涉及请求流转的服务如下如需记录均需修改
* 提供者client
* 服务消费者ribbon
* 服务消费者feign
* 路由网关服务zuul

以下为修改路由网关服务zuul为例
1. 添加链路追中的pom
```xml
<!--zipkin 服务追踪-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

2. 添加AlwaysSampler配置
```java
/**
 * 路由
 */
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class EurekazuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekazuulApplication.class, args);
    }
    
	@Bean
	public AlwaysSampler defaultSampler(){
		return new AlwaysSampler();
	}
}
```

3. 修改application.yml，添加链路追中服务器地址
```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8770
spring:
  application:
    name: eureka-zuul
  zipkin:
    base-url: http://eureka-zipkin
zuul:
 routes:
   api-a:
     path: /api-a/**
     serviceId: eureka-ribbon
   api-b:
     path: /api-b/**
     serviceId: eureka-feign
```

## 创建服务链路追踪服务zipkin
1. 首先创建一个maven主工程。
* groupId：online.qsx.demo
* artifactId：eureka-zipkin
* packaging：jar

2. 添加Spring Cloud的pom.xml依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>online.qsx.demo</groupId>
	<artifactId>eureka-zipkin</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<name>eureka-zipkin</name>
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
		<!--zuul -->
        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin-server</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin-autoconfigure-ui</artifactId>
        </dependency>
		<!--spring boot web -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!--spring boot 单元测试 -->
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
		<finalName>eureka-zipkin</finalName>
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
  port: 8780
spring:
  application:
    name: eureka-zipkin
```

4. 新建启动类EurekazipkinApplication.java
```java
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
```

5. run as 依次启动 服务注册中心(eureka server)、链路追踪(eureka zipkin)、多个服务提供者(eureka client)、服务消费者(eureka ribbon)、服务消费者(eureka feign)、路由网关(eureka zuul)

6. 访问：http://localhost:8761
![image](https://note.youdao.com/yws/public/resource/3210f9b0fd58edff649e013c237e54ea/xmlnote/C36D3FC65B044173A6A008566CBF40B7/9094)

7. 分别请求多次(只有请求过追踪服务器才会记录请求信息)
* http://localhost:8770/api-a//hi01?token=a
* http://localhost:8770/api-b//hi01?token=a

8. 访问：http://localhost:8780
![image](https://note.youdao.com/yws/public/resource/3210f9b0fd58edff649e013c237e54ea/xmlnote/9E0871FA547A47719D4BCD3EB69F5D58/9096)
![image](https://note.youdao.com/yws/public/resource/3210f9b0fd58edff649e013c237e54ea/xmlnote/D66E075CE35049609ED5ECE49EFE3CF5/9099)