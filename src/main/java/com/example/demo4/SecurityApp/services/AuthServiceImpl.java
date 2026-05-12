package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.LoginDto;
import com.example.demo4.SecurityApp.dto.LoginResponseDto;
import com.example.demo4.SecurityApp.entities.Session;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.exceptions.ResourceNotFoundException;
import com.example.demo4.SecurityApp.repositories.SessionRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;
    private final SessionService sessionService;

    @Value("${deploy.env}")
    private String deployEnv;

    @Override
    public LoginResponseDto logIn(LoginDto dto, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        if(authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            if(user == null) {
                throw new ResourceNotFoundException("No User found with the given Username - " + dto.getEmail());
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);


            log.info("accessToken : {}", accessToken);
            log.info("refreshToken : {}", refreshToken);
            sessionService.generateNewSession(user, refreshToken);

            Cookie cookie = new Cookie("refresh-token", refreshToken);

            cookie.setHttpOnly(true);
            //to make request https only
            cookie.setSecure("production".equals(deployEnv));

//            cookie.setMaxAge(7*60*60*24);
            response.addCookie(cookie);

            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setId(user.getId());
            loginResponseDto.setRefreshToken(refreshToken);
            loginResponseDto.setAccessToken(accessToken);
            return loginResponseDto;


        } else {
            throw new BadCredentialsException("Bad Credentials - No User Found");
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("refresh-token")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userService.findUserById(userId);

        //delete session for this refreshToken and user
        sessionService.deleteSession(user, refreshToken);

        //remove access Cookie
        Cookie accessCookie = new Cookie("access-token", null);
        accessCookie.setSecure(false);
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);

        //remove refresh cookie
        Cookie refreshCookie = new Cookie("refresh-token", null);
        refreshCookie.setSecure(false);
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
    }

    @Override
    public LoginResponseDto refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        sessionService.validateSession(refreshToken);
        User user = userService.findUserById(userId);

        String accessToken = jwtService.generateAccessToken(user);

        LoginResponseDto dto = new LoginResponseDto();
        dto.setId(user.getId());
        dto.setRefreshToken(refreshToken);
        dto.setAccessToken(accessToken);
        return dto;
    }
}
