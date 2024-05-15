package com.security.demo.jwt;

import com.security.demo.domain.dto.MemberEnum;
import com.security.demo.domain.entity.BlackListEntity;
import com.security.demo.domain.entity.UserEntity;
import com.security.demo.domain.entity.UserTokenEntity;
import com.security.demo.repository.BlackListRepository;
import com.security.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final BlackListRepository blackListRepository;

    /**
     * jwt secretKey 를 통해 secretKey 발급
     * - 사용자가 임의로 지정해준 jwt secretKey 를 통해서 JWTUtil 생성자에서 secretKey를 발급
     * @param secret : 임의로 지정한 secretKey
     */
    public JWTUtil(@Value("${spring.jwt.secret}") String secret,
                   UserRepository userRepository,
                   EntityManager entityManager,
                   BlackListRepository blackListRepository) {
        // 사용자가 임의로 지정해준 jwt secret key를 통해서 JWTUtil 생성자에서 secretKey를 발급
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.blackListRepository = blackListRepository;
    }

    /**
     * jwt token 에서 username 조회 메서드
     *
     * @param token : jwtToken
     * @return : username
     */
    public String getUsernameFromToken(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 token값을 통해 username 꺼낸 후 반환
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * jwt token 에서 role 조회 메서드
     *
     * @param token : jwtToken
     * @return : MemberEnum.class
     */
    public MemberEnum getRole(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 token값을 통해 role 꺼낸 후 반환
        return MemberEnum.valueOf(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class));
    }

    /**
     * jwt token 이 내가 만들어준 secretKey 로 생성한것인지 확인 메서드
     *
     * @param token : jwtToken
     * @return : boolean
     */
    public boolean isExpired(String token) {
        // 토큰이 우리 서버에서 발급된것인지 verifyWith 로 확인
        // 맞다면 getExpiration() 메서드를 통해 토큰이 expired 되었는지 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


    /**
     * logOut 시 호출할 메서드
     * - refreshToken 제거
     * - accessToken blackList 에 등록
     *
     * @param token : accessToken
     */
    @Transactional
    public void destroyToken(String token) {
        Claims payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        String username = payload.get("username", String.class);
        Date expirationTime = payload.getExpiration();

        UserEntity byUsername = userRepository.findByUsername(username);
        if (byUsername != null) {
            UserTokenEntity findTokenEntity = entityManager.find(UserTokenEntity.class, byUsername.getId());
            findTokenEntity.setRefreshToken(null);

            BlackListEntity blackListEntity = BlackListEntity.builder().build().ofBlackList(
                    byUsername.getId(),
                    token,
                    expirationTime
            );

            blackListRepository.save(blackListEntity);
        }
    }


    /**
     * blackList Table에 등록된 Token인지 검증
     * @param token : accessToken
     * @return : true or false
     */
    @Transactional
    public boolean verifyToken(String token) {
        return blackListRepository.existsByAccessToken(token);
    }

    /**
     * refreshToken 이 5분 내에 만료되는지 확인 메서드
     *
     * @param refreshToken : refreshToken
     * @return : boolean ( true : 5분 이내 만료 , false : 5분 이상 남음 )
     */
    public boolean isRefreshExpiredSoon(String refreshToken) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken).getPayload();

            // 토큰 만료날짜 추출
            Date expirationDate = claims.getExpiration();
            System.out.println("token expiration time : " + expirationDate);

            // 현재 시각에서 5분 더하기
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);
            Date thresholdDate = calendar.getTime();

            // 만료 날짜가 thresholdDate 이전 또는 같은 경우 true 반환
            return expirationDate.before(thresholdDate) || expirationDate.equals(thresholdDate);

        } catch (RuntimeException e) {
            throw new RuntimeException("잘못된 토큰 입력");
        }
    }



    /**
     * Jwt 토큰 생성 메서드
     * - 생성 대상 : accessToken , refreshToken
     *
     * @param username  : 사용자 이름
     * @param role      : 사용자 Role
     * @return : AccessToken , RefreshToken List 반환
     */
    public Map<String, String> createJwt(String username, MemberEnum role) {
        HashMap<String , String> tokenMap = new HashMap<>();

        // access jwt Token 생성, 만료시간 1분
        String accessToken = Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + JwtConstants.AT_EXP_TIME)) // 만료 시간 1분
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();

        // refresh jwt Token 생성, 만료시간 10분
        String refreshToken = Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + JwtConstants.RT_EXP_TIME)) // 만료 시간 10분
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();

        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

    /**
     * accessToken 생성 메서드
     *
     * @param role : role
     * @param username : username
     * @return : accessToken
     */
    public String createAccessToken(String username, MemberEnum role) {
        // access jwt Token 생성, 만료시간 1분
        return Jwts.builder()
                .claim("username", username) // JWT에 username 넣기
                .claim("role", role.toString())  // JWT에 Role 넣기
                .expiration(new Date(System.currentTimeMillis() + JwtConstants.AT_EXP_TIME)) // 만료 시간 1분
                .signWith(secretKey) // 사용자가 설정한 secretKey로 암호화
                .compact();
    }

}
