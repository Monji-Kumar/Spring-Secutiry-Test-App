package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.LoginDto;
import com.example.demo4.SecurityApp.dto.SignUpDto;
import com.example.demo4.SecurityApp.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto signup(SignUpDto dto);

    UserDetails loadUserByUsername(String username);
}
