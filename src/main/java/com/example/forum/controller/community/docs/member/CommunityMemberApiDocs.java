package com.example.forum.controller.community.docs.member;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.util.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Community Member", description = "Community Member related API")
public interface CommunityMemberApiDocs {

    @Operation(
            summary = "Get all users of a community",
            description = "Retrieves a list of all users who are members of the specified community.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of community members",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found",
                            content = @Content
                    )
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<UserDTO>>> getAllCommunityMembers(
            @Parameter(description = "ID of the community", required = true)
            @PathVariable Long communityId
    );

    @Operation(
            summary = "Get online users of a community",
            description = "Retrieves a list of users who are currently online in the specified community.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of online users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/online-users")
    ResponseEntity<CommonResponse<List<UserDTO>>> getOnlineUsers(
            @Parameter(description = "ID of the community to check online users", required = true)
            @PathVariable Long communityId
    );

    @Operation(
            summary = "Get new members of a community this week",
            description = "Returns a list of users who joined the specified community within the past 7 days.",
            parameters = {
                    @Parameter(
                            name = "communityId",
                            description = "ID of the community",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of new members",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/new-members")
    ResponseEntity<CommonResponse<List<UserDTO>>> getNewMembers(
            @PathVariable Long communityId
    );
}
