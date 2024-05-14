package com.security.demo.service;

import com.security.demo.domain.dto.CustomUserDetails;
import com.security.demo.domain.entity.UserEntity;
import com.security.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByUsername(username);

        if (userData == null) {
            throw new UsernameNotFoundException(username);
        } else  {
            return new CustomUserDetails(userData);
        }


    }
}
