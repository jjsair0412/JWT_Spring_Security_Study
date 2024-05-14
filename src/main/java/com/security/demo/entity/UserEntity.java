package com.security.demo.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String role;

    public UserEntity ofUserEntity(String username, String password, String role) {
        return UserEntity.builder()
                .username(username)
                .role(role)
                .password(password)
                .build();
    }
}
