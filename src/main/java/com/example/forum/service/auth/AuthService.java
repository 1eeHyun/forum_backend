package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.MeResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;

public interface AuthService {

    void signup(SignupRequestDTO dto);
    LoginResponseDTO login(LoginRequestDTO dto);
    MeResponseDTO getCurrUser(String username);
}
