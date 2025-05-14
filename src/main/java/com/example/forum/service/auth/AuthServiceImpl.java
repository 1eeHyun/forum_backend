package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
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

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthValidator authValidator;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.default-profile-image}")
    private String defaultProfileImageUrl;

    @Transactional
    public void signup(SignupRequestDTO dto) {

        authValidator.validateSignup(dto.getUsername(), dto.getEmail());

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        Profile profile = Profile.builder()
                .nickname(dto.getNickname())
                .bio("")
                .imageUrl(defaultProfileImageUrl)
                .user(user)
                .build();

        user.setProfile(profile);
        userRepository.save(user);
        profileRepository.save(profile);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = authValidator.validateLogin(dto.getUsernameOrEmail(), dto.getPassword());

        String token = jwtTokenProvider.generateToken(user.getUsername());

        return new LoginResponseDTO(token);
    }
}
