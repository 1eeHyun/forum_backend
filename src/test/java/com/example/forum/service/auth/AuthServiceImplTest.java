package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.model.user.User;
import com.example.forum.repository.profile.ProfileRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.security.JwtTokenProvider;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthValidator authValidator;

    @BeforeEach
    void setup() {
        // You can use this if necessary
        ReflectionTestUtils.setField(authService, "defaultProfileImageUrl", "default.png");
    }

    @Test
    void signup_withValidData_shouldSaveUser() {
        // given
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");
        dto.setNickname("Tester");

        String encodedPassword = "encoded_pw";

        when(passwordEncoder.encode(dto.getPassword())).thenReturn(encodedPassword);

        // when
        authService.signup(dto);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertNotNull(savedUser.getProfile());
        assertEquals("Tester", savedUser.getProfile().getNickname());
        assertEquals("default.png", savedUser.getProfile().getImageUrl());
    }

    @Test
    void signup_withDuplicateUsername_shouldThrowException() {
        // given
        SignupRequestDTO dto = new SignupRequestDTO();
        dto.setUsername("duplicate");
        dto.setPassword("pw");
        dto.setEmail("dup@example.com");
        dto.setNickname("Dup");

        doThrow(new IllegalArgumentException("Username exists"))
                .when(authValidator).validateSignup(dto.getUsername(), dto.getEmail());

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(dto);
        });

        assertEquals("Username exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // given
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsernameOrEmail("testuser");
        dto.setPassword("password123");

        User mockUser = User.builder()
                .username("testuser")
                .build();

        when(authValidator.validateLogin(dto.getUsernameOrEmail(), dto.getPassword()))
                .thenReturn(mockUser);

        JwtTokenProvider mockJwtProvider = mock(JwtTokenProvider.class);
        String mockToken = "mock.jwt.token";

        // inject mocked jwtTokenProvider
        ReflectionTestUtils.setField(authService, "jwtTokenProvider", mockJwtProvider);
        when(mockJwtProvider.generateToken(mockUser.getUsername())).thenReturn(mockToken);

        // when
        LoginResponseDTO response = authService.login(dto);

        // then
        assertNotNull(response);
        assertEquals(mockToken, response.getToken());
    }
}