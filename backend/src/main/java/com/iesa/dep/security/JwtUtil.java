
package com.iesa.dep.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String secret = System.getenv().getOrDefault("ADMIN_JWT_SECRET", "cambia-esta-clave-por-una-muy-larga");
    private static final Key key = Keys.hmacShaKeyFor(secret.getBytes());
    private static final long expiration = 1000L * 60 * 60 * 24; // 24 horas

    public static String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(key)
                .compact();
    }

    public static String validateTokenAndGetUser(String token){
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return claims.getBody().getSubject();
        }catch(Exception e){
            return null;
        }
    }
}
