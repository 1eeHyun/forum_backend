package com.example.forum.controller.auth.api;

import com.example.forum.controller.auth.docs.AuthApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.auth.LoginRequestDTO;
import com.example.forum.dto.auth.LoginResponseDTO;
import com.example.forum.dto.auth.SignupRequestDTO;
import com.example.forum.dto.user.UserDTO;
import com.example.forum.service.auth.AuthService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<Void>> signup(@RequestBody SignupRequestDTO dto) {

        authService.signup(dto);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto) {

        LoginResponseDTO responseToken = authService.login(dto);
        return ResponseEntity.ok(CommonResponse.success(responseToken));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        authService.logout(username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<?>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        UserDTO response = authService.getCurrUser(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
