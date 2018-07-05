# SpringCloud-10-高可用的分布式配置中心(Config)

## 创建服务注册中心eureka-server
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 创建服务提供者client
* 参考：SpringCloud-01-服务的注册与发现（Eureka）

## 使用git 编写服务提供者client对应的配置
1. 分布式配置命名格式：服务名-版本.properties、服务名-版本.yml

2. 常见的版本：
* 开发环境配置文件：pro
* 测试环境配置文件：test
* 生产环境配置文件：dev

3. 示例：
* eureka-client-dev.properties
* eureka-client-test.yml

4. 提交到git效果如图
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/471694BC97244177807EE07C342478B8/9410)

## 创建配置中心
1. 首先创建一个maven主工程。
* groupId：online.qsx.demo
* artifactId：eureka-config
* packaging：jar

2. 添加Spring Cloud的pom.xml依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>online.qsx.demo</groupId>
	<artifactId>eureka-config</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<name>eureka-config</name>
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
		<!-- 配置服务中心 -->
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
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
		<finalName>eureka-config</finalName>
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
server:
  port: 10011
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
spring:
  application:
    name: eureka-config
  cloud:
    config:
      label: master
      server:
        git:
          uri: https://github.com/Innovaee2018/SpringCloud
          searchPaths: config
```
4. 新建启动类EurekaconfigApplication.java使用@EnableEurekaClient实现配置中心
```java
package online.qsx.demo.eurekaconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 配置中心服务
 */
@EnableEurekaClient
@EnableConfigServer
@SpringBootApplication
public class EurekaconfigApplication {

	public static void main(String[] args) {

        SpringApplication.run(EurekaconfigApplication.class, args);	
	}

}

```
5. run as 依次启动 服务注册中心(eureka server)、配置中心(eureka config)
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/82DF7F722B074903A5AE667C34475815/9336)
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/CB412BEFD0184F64BD20CFE2067678F2/9338)
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/4FC269BF3173452E9ABBD1EF78C58AD6/9340)



## 修改服务提供者client读取git远程配置
1. 添加pom依赖
```xml
<!-- 远程配置 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

2.修改启动类添加git中提供配置的属性value，验证远程配置加载
```java
package online.qsx.demo.eurekaclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

3.修改application.yml
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
```

4.添加配置加载bootstrap.yml
```yml
spring:
  cloud:
    config:
      label: master
      profile: pro
      discovery:
        enabled: true
        service-id: eureka-config
```

5. run as 依次启动 服务注册中心(eureka server)、多个配置中心(eureka config)[10011、10012、10013]、服务提供者(eureka client)
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/F9C0CD9E00BB456D8E40151E48ED84AB/9432)
![image](https://note.youdao.com/yws/public/resource/72338bb74a21b347573fb2da6437313a/xmlnote/73EAC257A30D4917B14D35B116C5D5AF/9342)