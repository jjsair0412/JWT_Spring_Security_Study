package com.security.demo.jwt;

import com.security.demo.dto.MemberEnum;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT 0.12.3 버전으로 JWT 발급
 * <p>
 * 각 메서드들은 token을 전달받아 내부 데이터를 확인함
 * - JWTUtil 생성자 : 사용자가 임의로 설정한 Key값으로 SecretKey 발급
 * - username 확인 메소드 : getUsername(String token)
 * - role 확인 메소드 : getRole(String token)
 * - 만료일 확인 메소드 : isExpired(String token)
 * - 로그인 완료시 JWT 토큰 생성 후 반환 메소드 : createJwt(String username, String role, Long expiredMs)
 */
@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // 사용자가 임의로 지정해준 jwt secret key를 통해서 JWTUtil 생성자에서 secretKey를 발급
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsernameFromToken(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 token값을 통해 username 꺼낸 후 반환
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public MemberEnum getRole(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 token값을 통해 role 꺼낸 후 반환
        return MemberEnum.valueOf(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class));
    }

    public boolean isExpired(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 getExpiration() 메서드를 통해 토큰이 expired 되었는지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * Jwt 토큰 생성 메서드
     *
     * @param username  : 사용자 이름
     * @param role      : 사용자 Role
     * @return : AccessToken , RefreshToken List 반환
     */
    public Map<String, String> createJwt(String username, MemberEnum role) {
        HashMap<String , String> tokenMap = new HashMap<>();

        // access jwt Token 생성, 1시간
        String accessToken = Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 10L)) // JWT에 만료시간 넣기
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();

        // refresh jwt Token 생성, 2주
        String refreshToken = Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + 60L * 60L * 24L * 7L * 2L)) // JWT에 만료시간 넣기
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();

        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

    // AccessToken 만료 시 재발급 코드
    public String reIssuanceAccessToken(String username, MemberEnum role) {
        // access jwt Token 생성, 1시간
        return Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 10L)) // JWT에 만료시간 넣기
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();
    }
}
