package com.security.demo.domain.entity;

import com.security.demo.domain.dto.MemberEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String password;

    @Column(nullable = false)
    private String role;

    @OneToOne
    @JoinColumn(name = "usertoken_id")
    private UserTokenEntity userTokenEntity;

    public UserEntity() {}

    public UserEntity ofUserEntity(String username, String password, String role) {
        return UserEntity.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
