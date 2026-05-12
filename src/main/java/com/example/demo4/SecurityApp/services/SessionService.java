package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.User;

public interface SessionService {

    void generateNewSession(User user, String refreshToken);

    void validateSession(String refreshToken);

    void deleteSession(String refreshToken);

    void deleteSession(User user, String refreshToken);
}
