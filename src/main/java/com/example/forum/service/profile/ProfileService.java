package com.example.forum.service.profile;

import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.profile.*;

public interface ProfileService {

    ProfileResponseDTO getProfile(String targetUsername, String loginUsername);

    void updateNickname(String username, NicknameUpdateDTO dto);
    LoginResponseDTO updateUsername(String currUsername, UsernameUpdateDTO dto);
    void updateBio(String username, BioUpdateDTO dto);
    void updateProfileImage(String username, ProfileImageUpdateDTO dto);

//    ProfileResponseDTO getProfileByPublicId(String publicId);
}
