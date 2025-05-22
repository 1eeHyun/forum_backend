package com.example.forum.dto.post;

import com.example.forum.model.post.Visibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostRequestDTO {

    @NotBlank
    private String title;

    private String content;
    private Visibility visibility;
    private List<String> imageUrls;
    private Long communityId; // Optional: community selection
}
