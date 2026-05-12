package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.Session;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;

    @Override
    public void generateNewSession(User user, String refreshToken) {
        List<Session> sessions = sessionRepository.findByUser(user);

        if(sessions.size() >= SESSION_LIMIT) {
            sessions.sort(Comparator.comparing(Session::getLastUsedAt));

            Session earliestSession = sessions.getFirst();
            sessionRepository.delete(earliestSession);
        }

        Session newSession = new Session();
        newSession.setUser(user);
        newSession.setRefreshToken(refreshToken);
        sessionRepository.save(newSession);
    }

    @Override
    public void validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new SessionException("No Valid Session found for the given Refresh Token"));
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public void deleteSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new SessionException("No Valid Session found for the given Refresh Token"));
        sessionRepository.delete(session);
    }

    @Override
    public void deleteSession(User user, String refreshToken) {
        Optional<Session> sessionOpt = sessionRepository.findByUserAndRefreshToken(user, refreshToken);
        if(sessionOpt.isPresent()) {
            sessionRepository.delete(sessionOpt.get());
        }
    }
}
