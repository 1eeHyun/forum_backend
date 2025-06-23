package com.example.forum.dto.community;

import com.example.forum.dto.image.ImageDTO;
import com.example.forum.dto.user.UserDTO;
import com.example.forum.model.community.CommunityRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDetailDTO {

    private Long id;
    private String name;
    private String description;

    private ImageDTO profileImageDto;
    private String bannerImageUrl;

    private LocalDateTime createdAt;

    private List<CommunityRuleResponseDTO> rules;
    private List<CategoryResponseDTO> categories;

    private int memberCount;
    private int onlineCount;

    private CommunityRole role;

    private List<UserDTO> onlineUsers;
}
