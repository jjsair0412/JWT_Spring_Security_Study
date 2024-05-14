package com.security.demo.repository;

import com.security.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    // 로그인 검증용 Username 찾기
    UserEntity findByUsername(String username);
}
