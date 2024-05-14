package com.security.demo.dto;

import com.security.demo.entity.UserEntity;
import com.security.demo.entity.UserTokenEntity;
import com.security.demo.jwt.JWTUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@Builder
public class JoinDto {
    private String username;
    private String password;
    private MemberEnum memberRole;


    public UserEntity toUserEntity(JoinDto joinDto, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return UserEntity.builder()
                .username(joinDto.getUsername())
                .password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
                .role(joinDto.getMemberRole().name())
                .build();
    }

}
