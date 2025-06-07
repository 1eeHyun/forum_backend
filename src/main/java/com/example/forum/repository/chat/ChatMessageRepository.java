package com.example.forum.repository.chat;

import com.example.forum.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @EntityGraph(attributePaths = {"sender", "sender.profile"})
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);

    Optional<ChatMessage> findTopByRoomIdOrderBySentAtDesc(String roomId);
}
