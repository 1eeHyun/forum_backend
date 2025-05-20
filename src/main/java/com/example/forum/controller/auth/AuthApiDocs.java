package com.example.forum.controller.auth;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Operation(
            summary = "Logout",
            description = "Handles logging out."
    )
    ResponseEntity<CommonResponse<Void>> logout(UserDetails userDetails);

    @Operation(
            summary = "Get current user's info",
            description = "Retrieves current logged-in user's information."
    )
    ResponseEntity<CommonResponse<?>> getCurrentUser(UserDetails userDetails);
}
