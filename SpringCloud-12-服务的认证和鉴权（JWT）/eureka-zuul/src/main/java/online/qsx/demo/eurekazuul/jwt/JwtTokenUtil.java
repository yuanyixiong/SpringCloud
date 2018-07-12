/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
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

/**
 * @ClassName JwtTokenUtil
 * @Description TODO
 * @Date 2018/07/11 15:02
 * @Author yuan yi xiong
 * @Version 1.0
 **/
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
