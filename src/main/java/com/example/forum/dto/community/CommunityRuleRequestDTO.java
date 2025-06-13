package com.example.forum.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityRuleRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
