package com.example.forum.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineUserDTO {

    private Long id;
    private String nickname;
    private ImageDTO imageDto;
}
