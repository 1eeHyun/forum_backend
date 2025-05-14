package com.example.forum.service.profile;

import com.example.forum.dto.profile.ProfileResponseDTO;

public interface ProfileService {

    ProfileResponseDTO getProfile(String targetUsername, String loginUsername);
}
