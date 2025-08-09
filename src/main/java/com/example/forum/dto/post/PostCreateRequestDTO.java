package com.example.forum.dto.post;

import com.example.forum.model.post.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDTO {

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 150, message = "Title must be less than 150 characters.")
    private String title;

    @Size(max = 5000, message = "Content must be less than 5000 characters.")
    private String content; // optional

    @NotNull(message = "Visibility must be provided.")
    private Visibility visibility;

    private List<PostFileDTO> fileUrls;

    @Size(max = 5, message = "Up to 5 tags are allowed.")
    private List<
            @Size(max = 50, message = "Tag must be <= 50 characters.")
            @Pattern(regexp = ".*\\S.*", message = "Tag cannot be whitespace only.")
                    String
            > tags;

    private Long categoryId; // Optional: community's category selection
}
