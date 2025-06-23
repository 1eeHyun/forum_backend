package com.example.forum.service.auth;

import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.dto.user.UserDTO;

public interface AuthService {

    void signup(SignupRequestDTO dto);
    LoginResponseDTO login(LoginRequestDTO dto);
    void logout(String username);
    UserDTO getCurrUser(String username);
}
