package com.example.forum.dto.post;

import com.example.forum.model.post.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 150, message = "Title must be less than 150 characters.")
    private String title;

    @Size(max = 5000, message = "Content must be less than 5000 characters.")
    private String content; // optional

    @NotNull(message = "Visibility must be provided.")
    private Visibility visibility;

    private List<String> imageUrls;

    private Long communityId; // Optional: community selection
}
