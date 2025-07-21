package com.auth.utills;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

    @Value("{jwt.secret.key}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> cliams = new HashMap<>();
        return createToken(cliams, userDetails.getUsername());

    }

    private String createToken(Map<String, Object> cliams, String email) {
        return Jwts.builder()
                .setClaims(cliams)
                .setSubject(email).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // private Claims extractAllClaims(String token){
    //     return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    // }
    private Claims extractAllClaims(String token) {
    try {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    } catch (Exception e) {
        System.out.println("Invalid token format: " + token);
        throw e; // Let the filter handle this
    }
}


    public <T> T extractClaims(String token , Function<Claims , T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
        
    }

    public String extractEmail(String token){
      return  extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
      return   extractExpiration(token).before(new Date());

    }

    public Boolean validateToken(String token , UserDetails userDetails ){
        final  String email = extractEmail(token);
        return  (email.equals(userDetails.getUsername()) && ! isTokenExpired(token));
    }

}
