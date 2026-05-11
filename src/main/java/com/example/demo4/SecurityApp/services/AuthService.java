package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    String logIn(LoginDto dto, HttpServletRequest request);
}
