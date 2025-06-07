package com.example.forum.controller.chat.api;

import com.example.forum.controller.chat.docs.ChatApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.chat.ChatRoomDTO;
import com.example.forum.dto.chat.ChatRoomRequestDTO;
import com.example.forum.service.chat.ChatService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController implements ChatApiDocs {

    private final ChatService chatService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<List<ChatMessageDTO>>> getMessages(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currUsername = authValidator.extractUsername(userDetails);

        List<ChatMessageDTO> response = chatService.getMessage(roomId, currUsername);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<String>> getOrCreateRoom(
            @RequestBody ChatRoomRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String response = chatService.getOrCreateRoomId(request.getUser1Username(), request.getUser2Username());

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<ChatRoomDTO>>> getMyChatRooms(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        List<ChatRoomDTO> rooms = chatService.getUserChatRooms(username);

        return ResponseEntity.ok(CommonResponse.success(rooms));
    }
}
