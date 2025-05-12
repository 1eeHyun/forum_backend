package com.example.forum.service.auth;

import com.example.forum.dto.auth.SignupRequestDTO;

public interface AuthService {

    void signup(SignupRequestDTO dto);
}
