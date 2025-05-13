package com.example.forum.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
