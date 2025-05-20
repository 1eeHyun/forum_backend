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

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommunityMember> members;

    @ElementCollection
    @CollectionTable(name = "community_rules", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "rule")
    private Set<String> rules = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "community_categories", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "category")
    private Set<String> categories = new HashSet<>();

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
        this.members.removeIf(cm -> cm.getUser().equals(user));
    }

}
