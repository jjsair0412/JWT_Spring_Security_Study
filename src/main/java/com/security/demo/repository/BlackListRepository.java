package com.security.demo.repository;

import com.security.demo.domain.entity.BlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackListEntity, Integer> {
    boolean existsByAccessToken(String token);
}
