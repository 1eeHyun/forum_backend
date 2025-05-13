package com.example.forum.dto.post;

import com.example.forum.model.post.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRequestDTO {

    @NotBlank
    private String title;

    private String content;

    private Visibility visibility;
    private Long communityId; // Optional: community selection
}
