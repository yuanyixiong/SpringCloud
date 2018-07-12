# SpringCloud-12-服务的认证和鉴权（JWT）

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

## JWT简介
### 为什么要使用JWT?
在微服务架构下的服务基本都是无状态的，传统的使用session的方式不再适用，如果使用的话需要做同步session机制，所以产生了了一些技术来对微服务架构进行保护，例如常用的鉴权框架Spring Security OAuth2和用Jwt来进行保护,相对于框架而言，jwt较轻，且可以自包含一些用户信息和设置过期时间，省去了Spring Security OAuth2繁琐的步骤。

### 什么是JWT？
jwt(JSON WEB TOKEN)是一种用来在网络上声明某种身份的令牌（TOKEN），它的特点是紧凑且自包含并且基于JSON，通过一些常用的算法对包含的主体信息进行加密，安全性很高。它通常有三个部分组成：头信息(Header),消息体（Payload）,签名（Signature）。
Header通常用来声明令牌的类型和使用的算法，Payload主要用来包含用户的一些信息，Signature部分则是将Base64编码后的Header和Payload进行签名。

### 在Spring Cloud下如何使用JWT?
在SC（Spring Cloud简称，以下将都采用这种方式）下通常使用需要安全保护的有两处，分别为系统认证和服务内部鉴权。

1. 系统认证基本流程

![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/84F537EECFD44F71B1C60C7815DA46A2/9518)

## 修改路由网关服务zuul实现JWT
1. 修改pom.xml添加jwt依赖
```xml
<!--jwt-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
```

2. 修改pom.xml添加配置spring security依赖
```xml
 <!--spring security-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency>
```

3. 配置spring mvc
```java
package online.qsx.demo.eurekazuul.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/403").setViewName("403");
    }
}
```

4. 配置spring security
```java
package online.qsx.demo.eurekazuul.config;

import online.qsx.demo.eurekazuul.jwt.JwtAuthenticationTokenFilter;
import online.qsx.demo.eurekazuul.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysUserService sysUserService;

    /**
     * 密码授权
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //指定用户登陆的Service
        auth.userDetailsService(sysUserService)
                //密码加密/解密
                .passwordEncoder(new PasswordEncoder() {
                    @Override
                    public String encode(CharSequence rawPassword) {
                        return rawPassword.toString();
                    }

                    @Override
                    public boolean matches(CharSequence charSequence, String encodedPassword) {
                        return Objects.equals(charSequence, encodedPassword);
                    }
                });
    }

    /**
     * 权限配置
     *
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        logger.info("开始权限配置");
        httpSecurity
                .csrf().disable()// 由于使用的是JWT，这里不需要csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()// 基于token，所以不需要session
                .authorizeRequests()//[访问URL][开始配置]
                .antMatchers("/auth/**").permitAll()// 允许匿名访问
                .anyRequest().authenticated() //尚未匹配的URL要求用户进行身份验证
                .and()//[访问URL][结束配置]
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())//[访问未经授权处理]
                .and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)// 添加JWT filter
                .headers().cacheControl();// 禁用缓存
        logger.info("完成权限配置");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 访问未经授权处理
     *
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, accessDeniedException) -> {
            logger.info("访问未经授权处理");
            String header = httpServletRequest.getHeader("X-Requested-With");
            boolean isAjax = header != null && "XMLHttpRequest".equals(header);
            //是否是ajax请求
            if (!isAjax) {
                logger.info("同步请求权限不足");
                httpServletResponse.sendRedirect("/403");
            } else {
                logger.info("ajax请求权限不足");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("text/plain");
                httpServletResponse.getWriter().write("权限不足");
                httpServletResponse.getWriter().close();
            }
        };
    }

    /**
     * 登陆认证成功处理,完成加载用户菜单
     *
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            logger.info("登入认证成功");
            //检查当前用户是否拥有登入系统的权限
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                logger.info(grantedAuthority.getAuthority());
            }
            //当前登入用户,有登入系统的角色权限[去首页]
            httpServletRequest.getRequestDispatcher("/").forward(httpServletRequest, httpServletResponse);
        };
    }

    /**
     * 引入JWT
     *
     * @return
     * @throws Exception
     */
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

}
```

5. 添加权限架构实体
```java
package online.qsx.demo.eurekazuul.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysRole {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long id;

    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SysRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SysRole() {
    }
}

```
```java
package online.qsx.demo.eurekazuul.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SysUser implements UserDetails {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long id;

    private String username;

    private String password;

    private String signingKey;

    private Date lastPasswordResetDate;

    private List<SysRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        logger.info(String.format("给%s设置spring security权限", this.toString()));
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.stream().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", lastPasswordResetDate=" + lastPasswordResetDate +
                ", roles=" + roles +
                '}';
    }

    public SysUser(Long id, String username, String password, Date lastPasswordResetDate, String signingKey, List<SysRole> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.signingKey = signingKey;
        this.roles = roles;
    }

    public SysUser() {
    }
}
```
6. 实现JWT工具
```java
package online.qsx.demo.eurekazuul.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class JwtAuthenticationResponse implements Serializable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
```
```java
package online.qsx.demo.eurekazuul.jwt;

import online.qsx.demo.eurekazuul.dto.SysUser;
import online.qsx.demo.eurekazuul.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysUserService sysUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            logger.info("checking authentication " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SysUser sysUser = this.sysUserService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, sysUser)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
```
```java
package online.qsx.demo.eurekazuul.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import online.qsx.demo.eurekazuul.dto.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -3301605591108950415L;

    @Value("${jwt.signingKey}")
    private String signingKey = "666666"; //加密

    @Value("${jwt.created}")
    private String created = "created";  //创建时间

    @Value("${jwt.sub}")
    private String sub = "sub"; //用户名

    @Value("${jwt.expiration}")
    private long expiration = 10;  //秒

    /**
     * 验证用户的token
     * 1.账号一致
     * 2.token没过期
     * 3.token创建后用户密码未发生修改
     *
     * @param token
     * @param sysUser
     * @return
     */
    public Boolean validateToken(String token, SysUser sysUser) {
        logger.info("[validateToken][token没过期/token创建后用户密码未发生修改/token创建后用户密码未发生修改]验证用户的token");
        final String username = getUsernameFromToken(token);
        final Date created = getCreatedDateFromToken(token);
        final Date expiration = getExpirationDateFromToken(token);
        return (username.equals(sysUser.getUsername()) && !expiration.before(new Date()) && !created.before(sysUser.getLastPasswordResetDate()));
    }

    /**
     * 生成Token
     *
     * @param sysUser
     * @return
     */
    public String generateToken(SysUser sysUser) {
        logger.info("[generateToken]生成Token");
        Map<String, Object> claims = new HashMap<>();
        claims.put(this.sub, sysUser.getUsername());
        claims.put(this.created, new Date());
        return Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + (this.expiration * 60 * 1000))).signWith(SignatureAlgorithm.HS512, this.signingKey).compact();
    }

    /**
     * 获取Token中保存的账户
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        logger.info("[getUsernameFromToken]获取Token中保存的账户");
        return Jwts.parser().setSigningKey(this.signingKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 获取Token指定的销毁时间
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        logger.info("[getExpirationDateFromToken]获取Token指定的销毁时间");
        return Jwts.parser().setSigningKey(this.signingKey).parseClaimsJws(token).getBody().getExpiration();
    }

    /**
     * 获取Token中保存的创建时间
     *
     * @param token
     * @return
     */
    public Date getCreatedDateFromToken(String token) {
        logger.info("[getCreatedDateFromToken]获取Token中保存的创建时间");
        return new Date((Long) Jwts.parser().setSigningKey(this.signingKey).parseClaimsJws(token).getBody().get(this.created));
    }
}
```
7. 完成登陆授权
```java
package online.qsx.demo.eurekazuul.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Massage {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String title;

    private String content;

    private String extraInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Massage(String title, String content, String extraInfo) {
        this.title = title;
        this.content = content;
        this.extraInfo = extraInfo;
    }

    public Massage() {
    }
}
```
```java
package online.qsx.demo.eurekazuul.service;

import online.qsx.demo.eurekazuul.dto.SysUser;
import online.qsx.demo.eurekazuul.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 登陆，授予Token
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        logger.info("登陆，授予Token");
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Reload password post-security so we can generate token
        final SysUser sysUser = sysUserService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(sysUser);
        return token;
    }

}

```
```java
package online.qsx.demo.eurekazuul.service;



import online.qsx.demo.eurekazuul.dto.SysRole;
import online.qsx.demo.eurekazuul.dto.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SysUserService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Objects.isNull(username) || !Objects.equals("admin", username)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<SysRole> roles = new ArrayList<SysRole>() {{
            add(new SysRole(1l, "ROLE_ADMIN"));
            //add(new SysRole(2l, "ROLE_USER"));
        }};

        Date time = null;
        try {
            time = new SimpleDateFormat("yyyy-mm-dd").parse("2012-12-12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SysUser(1001l, "admin", "123456", time, "666666", roles);
    }
}

```
```java
package online.qsx.demo.eurekazuul.controller;

import online.qsx.demo.eurekazuul.jwt.JwtAuthenticationResponse;
import online.qsx.demo.eurekazuul.service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Value("${jwt.header}")
    private String tokenHeader;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(String username, String password) throws AuthenticationException {
        final String token = authServiceImpl.login(username, password);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
}

```
```java
package online.qsx.demo.eurekazuul.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/")
    public ModelAndView index(ModelAndView mav) {
        logger.info("HomeController index");
        mav.addObject("msg", "测试标题");
        mav.setViewName("index");
        return mav;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping(value = "/admin/test1")
    @ResponseBody
    public String adminTest1() {
        logger.info("HomeController adminTest1-->ROLE_USER");
        return "ROLE_USER";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping("/admin/test2")
    @ResponseBody
    public String adminTest2() {
        logger.info("HomeController adminTest2-->ROLE_ADMIN");
        return "ROLE_ADMIN";
    }

}

```

## run as 依次启动 
1. 服务注册中心(eureka server)
2. 多个服务提供者(eureka client)
3. 服务消费者(eureka ribbon)
4. 服务消费者(eureka feign)
5. 路由网关(eureka zuul)

## 使用postman模拟请求测试
1. 获取token
![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/49D1F95DC8DC456EB970FB62DFB3CD05/9591)

2. 使用token访问eureka-zull服务的API
![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/B8745C80F56D4A039AC21368740453CC/9593)

3. 使用token访问eureka-zull服务路由后的eureka-ribbon服务的API
![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/4CBC990A299C43498C91020002FE0336/9595)

4. 使用token访问eureka-zull服务路由后的eureka-feign服务的API
![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/CB22BF652B3048C48D6341E4B1148CC6/9597)

5. 不携带token访问
![image](https://note.youdao.com/yws/public/resource/455d50d8f1b7c71e4b7a357b490390ee/xmlnote/19FC9AC868984C27B18908B3313CF606/9607)