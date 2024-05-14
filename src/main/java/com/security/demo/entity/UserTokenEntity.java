package com.security.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tokenId", nullable = false)
    private int id;
    private String refreshToken;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserEntity userEntity;

    public UserTokenEntity ofUserTokenEntity(UserEntity userEntity, String refreshToken) {
        return UserTokenEntity.builder()
                .id(userEntity.getId())
                .refreshToken(refreshToken)
                .userEntity(userEntity)
                .build();
    }

}
