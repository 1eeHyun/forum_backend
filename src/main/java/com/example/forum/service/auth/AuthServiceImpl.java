package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.MeResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.profile.ProfileRepository;
import com.example.forum.repository.user.UserRepository;
import com.example.forum.security.JwtTokenProvider;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    // Validators
    private final AuthValidator authValidator;

    // Repositories
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // Services

    // Security
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Default value
    @Value("${app.default-profile-image}")
    private String defaultProfileImageUrl;

    /**
     * This method handles signing up
     * @param dto
     */
    @Override
    @Transactional
    public void signup(SignupRequestDTO dto) {

        // Check if dto values are valid(Unique): username and email
        authValidator.validateSignup(dto.getUsername(), dto.getEmail());

        // Create a new user
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        // Create a new profile
        Profile profile = Profile.builder()
                .nickname(dto.getNickname())
                .bio("")
                .imageUrl(defaultProfileImageUrl)
                .user(user)
                .build();

        // Connect the new profile to the new user object
        user.setProfile(profile);
        userRepository.save(user);
        profileRepository.save(profile);
    }

    /**
     * This method handles signing in
     * @param dto
     * @return LoginResponseDTO
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        // Validates input values. If user's username or email matches with the password
        User user = authValidator.validateLogin(dto.getUsernameOrEmail(), dto.getPassword());

        // Provides a token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        // Marks the user as online
        markUserOnline(user);

        // Return Response by calling its constructor passing the generated token and their username
        return new LoginResponseDTO(token, user.getUsername());
    }

    /**
     * This method handles logging out
     * @param username
     */
    @Override
    public void logout(String username) {

        // Retrieves currently requesting user
        User user = authValidator.validateUserByUsername(username);
        markUserOffline(user); // Marks the user as offline
    }

    /**
     * This method handles current user's information including:
     *  username
     *  email
     *  nickname
     *  profile image
     * @param username
     * @return
     */
    @Override
    public MeResponseDTO getCurrUser(String username) {

        // Retrieves currently requesting user
        User user = authValidator.validateUserByUsername(username);
        return AuthorMapper.toMeDto(user);
    }

    // Helper methods
    private void markUserOnline(User user) {
        user.setOnline(true);
        userRepository.save(user);
    }

    private void markUserOffline(User user) {
        user.setOnline(false);
        userRepository.save(user);
    }
}
