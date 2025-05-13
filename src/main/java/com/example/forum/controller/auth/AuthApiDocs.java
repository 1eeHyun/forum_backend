package com.example.forum.controller.auth;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Authentication related API")
public interface AuthApiDocs {

    @Operation(
            summary = "Sign up",
            description = "Registers a new user account with username, password, email, and profile details."
    )
    ResponseEntity<CommonResponse<Void>> signup(SignupRequestDTO dto);

    @Operation(
            summary = "Login",
            description = "Authenticates a user with username and password, and returns a JWT access token upon success."
    )
    ResponseEntity<CommonResponse<LoginResponseDTO>> login(LoginRequestDTO dto);
}
