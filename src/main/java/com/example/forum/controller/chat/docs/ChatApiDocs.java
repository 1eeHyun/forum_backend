package com.example.forum.controller.chat.docs;


import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.chat.ChatRoomDTO;
import com.example.forum.dto.chat.ChatRoomRequestDTO;
import com.example.forum.dto.chat.MarkAsReadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/rooms/{roomId}/messages")
    ResponseEntity<CommonResponse<Map<String, Object>>> getMessages(
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
    @PostMapping("/rooms")
    ResponseEntity<CommonResponse<String>> getOrCreateRoom(
            @Parameter(description = "ID of the first and second user dto", required = true)
            @RequestBody ChatRoomRequestDTO request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get user's chat rooms",
            description = "Retrieves a list of chat rooms along with the last message for each room.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chat rooms retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in")
            }
    )
    @GetMapping("/rooms/my")
    ResponseEntity<CommonResponse<List<ChatRoomDTO>>> getMyChatRooms(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Mark messages as read in a chat room",
            description = "Marks messages up to the given message ID as read for the current user in the specified chat room.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Last read message ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MarkAsReadRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Messages marked as read successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "404", description = "Chat room not found")
            }
    )
    @PostMapping("/rooms/{roomId}/read")
    ResponseEntity<CommonResponse<Void>> markAsRead(
            @Parameter(description = "Chat room ID", example = "abc123")
            @PathVariable String roomId,

            @RequestBody MarkAsReadRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
