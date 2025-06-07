package com.example.forum.dto.profile;

import com.example.forum.dto.util.ImageDTO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePreviewDTO {

    private String username;
    private String nickname;
    private ImageDTO imageDto;
}
