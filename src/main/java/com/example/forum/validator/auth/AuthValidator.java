package com.example.forum.validator.auth;

import com.example.forum.exception.auth.*;
import com.example.forum.model.user.User;
import com.example.forum.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateSignup(String username, String email) {

        if (userRepository.existsByUsername(username))
            throw new DuplicateUsernameException();

        if (userRepository.existsByEmail(email))
            throw new DuplicateEmailException();
    }

    public User validateLogin(String usernameOrEmail, String password) {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidPasswordException();

        return user;
    }

    public User validateUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username))
            throw new DuplicateUsernameException();
    }

    public void validateSameUsername(String targetUsername, String username) {
        if (userRepository.findByUsername(targetUsername) != userRepository.findByUsername(username))
            throw new NotAuthorizedException();
    }

}
