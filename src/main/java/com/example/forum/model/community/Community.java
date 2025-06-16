package com.example.forum.model.community;

import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String profileImageUrl;

    @Column(nullable = true)
    private Double profileImagePositionX;

    @Column(nullable = true)
    private Double profileImagePositionY;

    @Column(nullable = true)
    private String bannerImageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommunityMember> members = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommunityRule> rules = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> categories = new HashSet<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addMember(User user, CommunityRole role) {
        CommunityMember member = CommunityMember.builder()
                .community(this)
                .user(user)
                .role(role)
                .build();
        this.members.add(member);
    }

    public void removeMember(User user) {

        CommunityMember target = this.members.stream()
                .filter(cm -> cm.getUser().equals(user))
                .findFirst()
                .orElse(null);

        if (target != null) {
            this.members.remove(target);
        }
    }

    public void addCategory(String categoryName) {
        Category category = Category.builder()
                .name(categoryName)
                .community(this)
                .build();
        this.categories.add(category);
    }
}
