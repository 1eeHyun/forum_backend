package com.example.forum.model.post;

import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hidden_posts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HiddenPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private LocalDateTime hiddenAt;

    public HiddenPost(User user, Post post) {
        this.user = user;
        this.post = post;
        this.hiddenAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (hiddenAt == null) {
            hiddenAt = LocalDateTime.now();
        }
    }
}
