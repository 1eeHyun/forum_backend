package com.example.forum.validator.auth;

import com.example.forum.exception.auth.*;
import com.example.forum.model.user.User;
import com.example.forum.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthValidator Unit Tests")
class AuthValidatorTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AuthValidator authValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("validateSignup - throws if username already exists")
    void validateSignup_shouldThrowIfUsernameExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);
        assertThrows(DuplicateUsernameException.class, () ->
                authValidator.validateSignup("john", "john@email.com"));
    }

    @Test
    @DisplayName("validateSignup - throws if email already exists")
    void validateSignup_shouldThrowIfEmailExists() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@email.com")).thenReturn(true);
        assertThrows(DuplicateEmailException.class, () ->
                authValidator.validateSignup("john", "john@email.com"));
    }

    @Test
    @DisplayName("validateLogin - succeeds with correct credentials")
    void validateLogin_success() {
        User user = User.builder().username("john").password("encoded").build();
        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        User result = authValidator.validateLogin("john", "raw");

        assertEquals("john", result.getUsername());
    }

    @Test
    @DisplayName("validateLogin - throws if user not found")
    void validateLogin_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () ->
                authValidator.validateLogin("john", "pwd"));
    }

    @Test
    @DisplayName("validateLogin - throws if password is invalid")
    void validateLogin_shouldThrowIfPasswordWrong() {
        User user = User.builder().username("john").password("encoded").build();
        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () ->
                authValidator.validateLogin("john", "wrong"));
    }

    @Test
    @DisplayName("validateLoggedIn - throws if UserDetails is null")
    void validateLoggedIn_shouldThrowIfNull() {
        assertThrows(UnauthorizedException.class, () ->
                authValidator.validateLoggedIn(null));
    }

    @Test
    @DisplayName("extractUsername - returns username from UserDetails")
    void extractUsername_shouldReturnUsernameIfLoggedIn() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john");

        String result = authValidator.extractUsername(userDetails);
        assertEquals("john", result);
    }

    @Test
    @DisplayName("validateUserByUsername - returns user if exists")
    void validateUserByUsername_success() {
        User user = User.builder().username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = authValidator.validateUserByUsername("john");

        assertEquals("john", result.getUsername());
    }

    @Test
    @DisplayName("validateUserByUsername - throws if user not found")
    void validateUserByUsername_shouldThrowIfNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () ->
                authValidator.validateUserByUsername("john"));
    }

    @Test
    @DisplayName("validateUniqueUsername - throws if username exists")
    void validateUniqueUsername_shouldThrowIfDuplicate() {
        when(userRepository.existsByUsername("john")).thenReturn(true);
        assertThrows(DuplicateUsernameException.class, () ->
                authValidator.validateUniqueUsername("john"));
    }

    @Test
    @DisplayName("validateSameUsername - throws if usernames are different")
    void validateSameUsername_shouldThrowIfNotSame() {
        assertThrows(ForbiddenException.class, () ->
                authValidator.validateSameUsername("admin", "john"));
    }

    @Test
    @DisplayName("validateSameUsername - passes if usernames are the same")
    void validateSameUsername_shouldPassIfSame() {
        assertDoesNotThrow(() ->
                authValidator.validateSameUsername("john", "john"));
    }
}
