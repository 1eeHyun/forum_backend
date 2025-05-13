package com.example.forum.controller.auth;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

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
}
