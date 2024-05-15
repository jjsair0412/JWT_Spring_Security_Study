package com.security.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blackListId", nullable = false)
    private int id;
    private String accessToken;
    private Date expiration;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserEntity userEntity;

    public BlackListEntity ofBlackList(int id, String accessToken, Date expiration) {
        return BlackListEntity.builder()
                .id(id)
                .accessToken(accessToken)
                .expiration(expiration)
                .build();
    }
}
