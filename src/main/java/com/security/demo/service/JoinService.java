package com.security.demo.service;

import com.security.demo.dto.JoinDto;
import com.security.demo.dto.MemberEnum;
import com.security.demo.entity.UserEntity;
import com.security.demo.entity.UserTokenEntity;
import com.security.demo.jwt.JWTUtil;
import com.security.demo.repository.UserRepository;
import com.security.demo.repository.UserTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JoinService implements UserManagerService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserTokenRepository userTokenRepository;
    private final JWTUtil jwtUtil;

    public void joinProcess(JoinDto joinDto){
        String username = joinDto.getUsername();

        /**
         * 이미 가입된 계정이 있을경우 return
         */
        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) {
            return;
        }

        /**
         * password 값은 원본 그대로 DB에 저장되면 안되기 때문에 ,
         * @Bean 객체로 등록한 BCryptPasswordEncoder 의 .encode() 메서드로 인코딩 시켜야 함.
         */
        UserEntity userEntity = joinDto.toUserEntity(joinDto, bCryptPasswordEncoder);

        userRepository.save(userEntity);

    }

    @Override
    public Map<String,String> accessTokenRefreshService(HttpServletRequest request, HttpServletResponse response) {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("AccessToken 이 존재하지 않습니다.");
        }

        // accessToken 꺼내오기
        String requestRefreshToken = authorizationHeader.substring(7);
        String username = jwtUtil.getUsernameFromToken(requestRefreshToken);
        MemberEnum role = jwtUtil.getRole(requestRefreshToken);

        UserEntity userEntity = userRepository.findByUsername(username);

        // accessToken , refreshToken 재발급
        // refreshToken 만료일 계산하여 5분 미만일 경우에만 refreshToken 재발급후 DB save
        if (jwtUtil.isRefreshExpiredSoon(requestRefreshToken)) {
            log.info("refreshToken 만료일 5분 미만 , accessToken, refreshToken 재발급 수행");
            Map<String, String> jwtTokens = jwtUtil.createJwt(username, role);
            String refreshToken = jwtTokens.get("refreshToken");

            // 재 발급한 refreshToken DB 저장
            userTokenRepository.save(
                    UserTokenEntity.builder().build().ofUserTokenEntity(userEntity, refreshToken)
            );

            // accessToken, refreshToken return
            return jwtTokens;
        } else {
            log.info("refreshToken 만료일 5분 이상 , accessToken 재발급 수행");
            // refreshToken 5일 이상 남아있을경우 accessToken만 재 발급
            // refreshToken은 DB 조회하여 가져와서 리턴
            String accessToken = jwtUtil.createAccessToken(username, role);
            UserTokenEntity userToken = userTokenRepository.findById(userEntity.getId())
                    .orElseThrow(() -> new RuntimeException("refreshToken DB 조회 오류."));

            return Map.of("accessToken", accessToken, "refreshToken", userToken.getRefreshToken());
        }
    }
}
