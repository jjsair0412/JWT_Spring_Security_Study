package com.security.demo.repository;

import com.security.demo.domain.entity.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Integer> {


}
