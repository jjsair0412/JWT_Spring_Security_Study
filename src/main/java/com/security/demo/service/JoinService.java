package com.security.demo.service;

import com.security.demo.dto.JoinDto;
import com.security.demo.entity.UserEntity;
import com.security.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService implements UserManagerService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
        UserEntity userEntity = JoinDto.builder().build().toUserEntity(joinDto, bCryptPasswordEncoder);
        userRepository.save(userEntity);


    }
}
