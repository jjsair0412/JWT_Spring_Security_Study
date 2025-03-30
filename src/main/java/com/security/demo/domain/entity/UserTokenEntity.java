package com.security.demo.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
public class UserTokenEntity {

    @Id
    private int id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String userName;

    @OneToOne(mappedBy = "userTokenEntity")
    private UserEntity userEntity;

    public UserTokenEntity() {}

    public UserTokenEntity ofUserTokenEntity(UserEntity byUsername, String refreshToken) {
        return UserTokenEntity.builder()
                .id(byUsername.getId())
                .userName(byUsername.getUsername())
                .refreshToken(refreshToken)
                .build();
    }
}
