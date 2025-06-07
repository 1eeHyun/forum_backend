package com.example.forum.repository.chat;

import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findAllByUser1OrUser2(User user1, User user2);
}
