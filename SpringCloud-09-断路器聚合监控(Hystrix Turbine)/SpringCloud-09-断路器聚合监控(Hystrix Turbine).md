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

## 服务消费者feign实现hystrix
* 参考：SpringCloud-07-断路器（Hystrix）

## 创建断路器聚合监控hystrix turbine
1. 首先创建一个maven主工程。
* groupId：online.qsx.demo
* artifactId：eureka-hystrix-turbine
* packaging：jar

2. 添加Spring Cloud的pom.xml依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>online.qsx.demo</groupId>
	<artifactId>eureka-hystrix-turbine</artifactId>
	<version>0.0.1-SNAPSHOT</version>


	<name>eureka-hystrix-turbine</name>
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
		<!-- 断路器聚合监控 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-turbine</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-netflix-turbine</artifactId>
		</dependency>
		<!-- 断路器仪表盘 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
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
		<finalName>eureka-hystrix-turbine</finalName>
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
  port: 8790
spring:
  application:
    name: eureka-hystrix-turbine
turbine:
  aggregator:
    clusterConfig: default
  appConfig: eureka-ribbon,eureka-feign
  clusterNameExpression: new String("default")
```

4. 新建启动类EurekahystrixturbineApplication.java
```java
package online.qsx.demo.eurekahystrixturbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableHystrixDashboard
@EnableTurbine
@SpringBootApplication
public class EurekahystrixturbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekahystrixturbineApplication.class, args);
	}

}

```

5. run as 依次启动 服务注册中心(eureka server)、服务提供者(eureka client)、服务消费者(eureka ribbon)、服务消费者(eureka feign)、断路聚合监控(eureka-hystrix-turbine)

6. 请求：http://localhost:8764/hi01 完成一次请求调度
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/99A427BCE4C14F718C671D442C44E196/9299)

7. 请求：http://localhost:8764/hystrix.stream 查看断路器(hystrix)监控数据
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/C0BD9BE4286B4E43B68930AE29CD21A9/9302)

8. 请求：http://localhost:8764/hi01 完成一次请求调度
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/780737CC34AE4C0DB06F74C827F97C5E/9305)

9. 请求：http://localhost:8764/hystrix.stream 查看断路器(hystrix)监控数据
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/6CF0B445EACF41F68E021D00B85D625F/9304)

10. 请求：http://localhost:8790/turbine.stream 查看断路器聚合监控(hystrix-turbine)监控数据
备注当前监控器汇总了eureka-ribbon、eureka-feign两个服务的监控数据
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/F4C3D25ADB0444E78DAD8703FFFB349B/9308)

11. 请求：http://localhost:8790/hystrix使用仪表监控汇总的数据
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/1CB530DCBB2E40ABAEE75991FD0B9A1E/9311)
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/2DD05E6290CB44098A24643B84E62DE5/9313)

12. 单击“Monitor Stream”查看仪表
![image](https://note.youdao.com/yws/public/resource/396e79ec03176e3b03fc10ddf30dde02/xmlnote/FD2B6A39235249D1A72FFCB9E36AF634/9316)
