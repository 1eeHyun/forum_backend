package com.example.forum.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class CommunityRequestDTO {

    private String name;
    private String description;
}
