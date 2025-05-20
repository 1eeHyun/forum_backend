package com.example.forum.controller.auth;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.MeResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.service.auth.AuthService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Void>> signup(@RequestBody SignupRequestDTO dto) {

        authService.signup(dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto) {

        LoginResponseDTO responseToken = authService.login(dto);
        return ResponseEntity.ok(CommonResponse.success(responseToken));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        authService.logout(username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<?>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        MeResponseDTO response = authService.getCurrUser(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
