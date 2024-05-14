package com.security.demo.controller;

import com.security.demo.domain.dto.MemberEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class MainController {


    @GetMapping("/")
    public String mainP(){
        /**
         * Jwt 방식은 stateless 방식이긴 하지만 , JWT 검증로직이 수행될 때 잠깐 세션에 값이 들어가있기 때문에 , 꺼내서 사용이 가능함.
         */
        // 잠깐 세션에 들어가있기 때문에 ID 추출
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // 잠깐 세션에 들어가있기 때문에 Role 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority next = iterator.next();
        MemberEnum memberEnum = MemberEnum.valueOf(next.getAuthority());

        return "Main Controller : " +name + " Role : " + memberEnum.name();
    }
}
