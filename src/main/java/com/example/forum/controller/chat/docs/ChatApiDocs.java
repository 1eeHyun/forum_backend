package com.example.forum.controller.chat.docs;


import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.chat.ChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Chat", description = "API related to chat messages and chat rooms")
public interface ChatApiDocs {

    @Operation(
            summary = "Get chat messages for a room",
            description = "Retrieves all chat messages in the specified room.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "404", description = "Chat room not found")
            }
    )
    @GetMapping("/chat/rooms/{roomId}/messages")
    ResponseEntity<CommonResponse<List<ChatMessageDTO>>> getMessages(
            @Parameter(description = "ID of the chat room", required = true)
            @PathVariable String roomId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get or create chat roomId for two users",
            description = "Returns the roomId for the given user pair. If the room does not exist, it will be created.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "RoomId retrieved or created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @PostMapping("/chat/rooms")
    ResponseEntity<CommonResponse<String>> getOrCreateRoom(
            @Parameter(description = "ID of the first user", required = true)
            @RequestParam Long user1Id,

            @Parameter(description = "ID of the second user", required = true)
            @RequestParam Long user2Id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}

