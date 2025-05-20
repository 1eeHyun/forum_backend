package com.example.forum.controller.community;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "Community", description = "Community related API")
public interface CommunityApiDocs {

    @Operation(
            summary = "Create a new community",
            description = "Create a new community"
    )
    ResponseEntity<CommonResponse<Long>> create(CommunityRequestDTO dto, UserDetails userDetails);

    @Operation(
            summary = "Retrieve a user's communities",
            description = "Retrieve a user's every community. Only logged-in user can do it."
    )
    ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getMyCommunities(UserDetails userDetails);

    @Operation(
            summary = "Retrieve a community",
            description = "Retrieve a community's information."
    )
    ResponseEntity<CommonResponse<CommunityDetailDTO>> getCommunity(Long id, UserDetails userDetails);
}
