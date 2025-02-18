package com.barogo.api.auth.service;

import com.barogo.api.auth.exception.InvalidUserException;
import com.barogo.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUserId(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUserId(),
                        user.getPassword(),
                        Collections.emptyList()
                ))
                .orElseThrow(() -> new InvalidUserException());
    }
}
