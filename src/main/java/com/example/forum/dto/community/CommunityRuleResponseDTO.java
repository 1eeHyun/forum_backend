package com.example.forum.dto.community;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "content", "createdAt"})
public class CommunityRuleResponseDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
