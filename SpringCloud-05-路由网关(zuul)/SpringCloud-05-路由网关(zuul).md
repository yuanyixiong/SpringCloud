# SpringCloud-05-路由网关(zuul)

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
1. 首先创建一个maven主工程。
* groupId：online.qsx.demo
* artifactId：eureka-zuul
* packaging：jar

2. 添加Spring Cloud的pom.xml依赖
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>online.qsx.demo</groupId>
	<artifactId>eureka-zuul</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<name>eureka-zuul</name>
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
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zuul</artifactId>
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
		<finalName>eureka-zuul</finalName>
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
  port: 8770
spring:
  application:
    name: eureka-zuul
zuul:
 routes:
   api-a:
     path: /api-a/**
     serviceId: eureka-ribbon
   api-b:
     path: /api-b/**
     serviceId: eureka-feign
```

4. 新建启动类EurekazuulApplication.java使用@EnableZuulProxy实现路由功能
ZuulFilter类的filterType方法返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型
* pre：路由之前
* routing：路由之时
* post： 路由之后
* error：发送错误调用
```java
package online.qsx.demo.eurekazuul;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

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
}

/**
 * 路由的过滤
 */
@Component
class MyFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(MyFilter.class);

    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     *
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 过滤的顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 这里可以写逻辑判断，是否要过滤，true,永远过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问
     * @return
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        logger.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if(accessToken == null) {
            logger.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}
            return null;
        }
        logger.info("ok");
        return null;
    }
}
```

5. run as 依次启动 服务注册中心(eureka server)、多个服务提供者(eureka client)、服务消费者(eureka ribbon)、服务消费者(eureka feign)、路由网关(eureka zuul)

6. 访问：http://localhost:8761
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/43CFA4C210CB495B83B7E9B2FF035605/8944)

7. 访问：http://localhost:8770/api-a
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/E6D00F058534483BAF1771BC6A82F713/8947)
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/34DF0E1F1AEB4270A79A71C11CF8DFC6/8949)
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/158A8618890B4D8EA93D993257970827/8953)

8. 访问：http://localhost:8770/api-b
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/0C57DC66E53C4FFABAEBE9EDF584A5B4/8958)
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/F7AFD174B7284560AE41AB250F05E4B4/8955)
![image](https://note.youdao.com/yws/public/resource/d5e6891dfb8d2075ef9bc9121b62cb97/xmlnote/E2029AA8E9CD475DB74A73C27CDA66AA/8961)