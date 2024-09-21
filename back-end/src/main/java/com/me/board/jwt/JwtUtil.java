package com.me.board.jwt;

import com.me.board.VO.UserVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Getter
@Log4j2
public class JwtUtil {
    private SecretKey secretKey;
    private SecretKey secretKeyRefresh;
    private long accessTokenExpiredMs;
    private long refreshTokenExpiredMs;

    private RedisTemplate<String, String> redisTemplate;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret, @Value("${spring.jwt.secret-refresh}")String secretRefresh,
        @Value("${spring.jwt.access-token-expiredms}") long ate, @Value("${spring.jwt.refresh-token-expiredms}") long rte,
        RedisTemplate<String, String> redisTemplate){
        this.secretKey = new SecretKeySpec(secret.getBytes((StandardCharsets.UTF_8)), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.secretKeyRefresh = new SecretKeySpec(secretRefresh.getBytes((StandardCharsets.UTF_8)), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.redisTemplate = redisTemplate;
        this.accessTokenExpiredMs = ate;
        this.refreshTokenExpiredMs = rte;
    }

    public String getUserName(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(String userName, String role){
        return Jwts.builder()
                .claim("username", userName)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String userName, String role){
        String refreshToken = Jwts.builder()
                .claim("username", userName)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredMs))
                .signWith(secretKeyRefresh)
                .compact();

        redisTemplate.opsForValue().set(
                userName,
                refreshToken,
                refreshTokenExpiredMs,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public Authentication getAuthenticationWrapper(String authStr, boolean checkExpired){
        String token = getTokenString(authStr);
        if(token == null)
            return null;

        // 로그아웃 여부 확인
        if(redisTemplate.opsForValue().get(token) != null) {
            log.info("user did logout");
            return null;
        }

        try{
            return getAuthentication(token, checkExpired);
        }catch (ExpiredJwtException e){
            return null;
        }
    }

    public String getTokenString(String authStr){
        if(authStr == null || !authStr.startsWith("Bearer ")) {
            return null;
        }
        String token = authStr.split(" ")[1];
        return token;
    }

    public Authentication getAuthentication(String token, boolean checkExpired) {

        Claims claims = parseData(token, checkExpired);

        List<SimpleGrantedAuthority> authorities = Arrays
                .stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserVo principal = new UserVo(claims.get("username").toString(), "", claims.get("role").toString());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Claims parseData(String token, boolean checkExpired) {
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build().parseSignedClaims(token).getPayload();
        }
        catch (ExpiredJwtException e){
            if(!checkExpired)
                return e.getClaims();
            throw e;
        }
    }

    public Long getExpiration(String accessToken){
        Date expiration = null;
        try {
            expiration = parseData(accessToken, true).getExpiration();
        }catch(ExpiredJwtException e) {
            return null;
        }

        long currentTime = new Date().getTime();
        return expiration.getTime() - currentTime;
    }
}
