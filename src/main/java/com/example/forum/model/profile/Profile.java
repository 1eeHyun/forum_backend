package com.example.forum.model.profile;

import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String bio;
    private String imageUrl;

    @Column(nullable = true)
    private Double imagePositionX;

    @Column(nullable = true)
    private Double imagePositionY;

    @Column(nullable = false, unique = true, updatable = false)
    private String publicId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime updatedAt;

//    @PrePersist
//    public void generatePublicId() {
//        if (this.publicId == null) {
//            this.publicId = UUID.randomUUID().toString();
//        }
//    }

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
