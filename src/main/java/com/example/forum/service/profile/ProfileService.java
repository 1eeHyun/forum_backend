package com.example.forum.service.profile;

import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.profile.*;

public interface ProfileService {

    ProfileResponseDTO getProfile(String targetUsername, String loginUsername);

    void updateNickname(String targetUsername, String username, NicknameUpdateDTO dto);
    LoginResponseDTO updateUsername(String targetUsername, String currUsername, UsernameUpdateDTO dto);
    void updateBio(String targetUsername, String username, BioUpdateDTO dto);
    void updateProfileImage(String targetUsername, String username, ProfileImageUpdateDTO dto);
}
