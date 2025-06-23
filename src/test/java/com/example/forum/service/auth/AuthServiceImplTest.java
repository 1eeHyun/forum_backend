package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.dto.user.UserDTO;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.profile.ProfileRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.security.JwtTokenProvider;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock private RedisService redisService;

    @Mock private UserRepository userRepository;

    @Mock private ProfileRepository profileRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private AuthValidator authValidator;

    @Mock private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Set default profile image URL for testing
        ReflectionTestUtils.setField(authService, "defaultProfileImageUrl", "default.png");
    }

    @Test
    @DisplayName("Should save user and profile when signup is successful")
    void signup_withValidData_shouldSaveUserAndProfile() {
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
        // Capture saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertNotNull(savedUser.getProfile());
        assertEquals("Tester", savedUser.getProfile().getNickname());
        assertEquals("default.png", savedUser.getProfile().getImageUrl());

        // Capture saved profile
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();

        assertEquals("Tester", savedProfile.getNickname());
        assertEquals(savedUser, savedProfile.getUser());
    }

    @Test
    @DisplayName("Should throw exception when signup with duplicate username or email")
    void signup_withDuplicateUsernameOrEmail_shouldThrowException() {
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
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return JWT token when login is successful")
    void login_withValidCredentials_shouldReturnToken() {
        // given
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsernameOrEmail("testuser");
        dto.setPassword("password123");

        User mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        when(authValidator.validateLogin(dto.getUsernameOrEmail(), dto.getPassword()))
                .thenReturn(mockUser);

        when(jwtTokenProvider.generateToken(mockUser.getUsername())).thenReturn("mock.jwt.token");

        // when
        LoginResponseDTO response = authService.login(dto);

        // then
        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("testuser", response.getUsername());

        verify(redisService).markUserOnline(1L);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when login fails due to invalid credentials")
    void login_withInvalidCredentials_shouldThrowException() {
        // given
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsernameOrEmail("wronguser");
        dto.setPassword("wrongpw");

        doThrow(new IllegalArgumentException("Invalid credentials"))
                .when(authValidator).validateLogin(dto.getUsernameOrEmail(), dto.getPassword());

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(dto);
        });

        assertEquals("Invalid credentials", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception if logout called with invalid username")
    void logout_withInvalidUsername_shouldThrowException() {
        // given
        String username = "notfound";
        doThrow(new IllegalArgumentException("User not found"))
                .when(authValidator).validateUserByUsername(username);

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.logout(username);
        });

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return MeResponseDTO for valid user")
    void getCurrUser_withValidUsername_shouldReturnMeResponseDTO() {
        // given
        String username = "testuser";
        User mockUser = User.builder().username(username).build();
        UserDTO meResponseDTO = UserDTO.builder()
                .username(username)
                .email("test@example.com")
                .nickname("Tester")
                .profileImage(null)
                .build();

        when(authValidator.validateUserByUsername(username)).thenReturn(mockUser);

        // Static mocking for AuthorMapper.toMeDto
        try (MockedStatic<com.example.forum.mapper.user.UserMapper> mapper =
                     mockStatic(com.example.forum.mapper.user.UserMapper.class)) {
            mapper.when(() -> com.example.forum.mapper.user.UserMapper.toDtoWithEmail(mockUser))
                    .thenReturn(meResponseDTO);

            // when
            UserDTO result = authService.getCurrUser(username);

            // then
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals("Tester", result.getNickname());
            verify(authValidator).validateUserByUsername(username);
        }
    }

    @Test
    @DisplayName("Should throw exception if getCurrUser called with invalid username")
    void getCurrUser_withInvalidUsername_shouldThrowException() {
        // given
        String username = "notfound";
        doThrow(new IllegalArgumentException("User not found"))
                .when(authValidator).validateUserByUsername(username);

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.getCurrUser(username);
        });

        assertEquals("User not found", ex.getMessage());
    }
}
