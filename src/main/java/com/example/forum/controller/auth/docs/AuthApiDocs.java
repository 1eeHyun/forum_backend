package com.example.forum.controller.auth.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.dto.util.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Auth", description = "Authentication related API")
public interface AuthApiDocs {

    @Operation(
            summary = "Sign up",
            description = "Registers a new user account with username, password, email, and profile details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful registration",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or duplicate username/email",
                            content = @Content
                    )
            }
    )
    @PostMapping("/signup")
    ResponseEntity<CommonResponse<Void>> signup(
            @RequestBody(
                    description = "Signup request body",
                    required = true
            )
            SignupRequestDTO dto
    );

    @Operation(
            summary = "Login",
            description = "Authenticates a user with username or email and password, and returns a JWT access token upon success.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid username/email or password",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - invalid input format",
                            content = @Content
                    )
            }
    )
    @PostMapping("/login")
    ResponseEntity<CommonResponse<LoginResponseDTO>> login(
            @RequestBody(
                    description = "Login request body containing username/email and password",
                    required = true
            )
            LoginRequestDTO dto
    );

    @Operation(
            summary = "Logout",
            description = "Logs out the currently authenticated user by marking them offline.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in or invalid token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found in database",
                            content = @Content
                    )
            }
    )
    @PostMapping("/logout")
    ResponseEntity<CommonResponse<Void>> logout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get current user's info",
            description = "Retrieves the currently logged-in user's profile information.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User info successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found in the system",
                            content = @Content
                    )
            }
    )
    @GetMapping("/me")
    ResponseEntity<CommonResponse<?>> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
