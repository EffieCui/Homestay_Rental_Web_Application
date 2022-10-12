package com.project.staybooking.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

// token的创建和解析
@Component
public class JwtUtil {
    @Value("${jwt.secret}") //去application property里找对应的值放在field
    private String secret;

    // 创建token
    //generate the JWT and return the encrypted result of it（网上有sample）
    public String generateToken(String subject) { // 生成token
        return Jwts.builder()
                .setClaims(new HashMap<>()) //placeholder
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, secret) //加密算法，生成乱码
                .compact();
    }

    // methods to decrypt a JWT from the encrypted value.
    // 解析token，乱码变成明文
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public Boolean validateToken(String token) {
        return extractExpiration(token).after(new Date());
    }



}
