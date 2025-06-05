package com.example.forum.dto.community;

import com.example.forum.dto.util.ImageDTO;
import com.example.forum.dto.util.OnlineUserDTO;
import com.example.forum.model.community.CommunityRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CommunityDetailDTO {

    private Long id;
    private String name;
    private String description;

    private ImageDTO profileImageDto;
    private String bannerImageUrl;

    private LocalDateTime createdAt;

    private List<String> rules;
    private List<CategoryResponseDTO> categories;

    private int memberCount;
    private int onlineCount;

    private CommunityRole role;

    private List<OnlineUserDTO> onlineUsers;
}
