package com.example.forum.repository.chat;

import com.example.forum.model.chat.ChatReadStatus;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {
    Optional<ChatReadStatus> findByRoomIdAndUser(String roomId, User user);
}
