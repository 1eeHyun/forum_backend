package com.example.forum.validator.auth;

import com.example.forum.exception.DuplicateEmailException;
import com.example.forum.exception.DuplicateUsernameException;
import com.example.forum.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;

    public void validateSignup(String username, String email) {

        if (userRepository.existsByUsername(username))
            throw new DuplicateUsernameException();

        if (userRepository.existsByEmail(email))
            throw new DuplicateEmailException();
    }
}
