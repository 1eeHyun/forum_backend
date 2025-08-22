package com.example.forum.model.community;

import com.example.forum.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community_members")
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CommunityRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    public CommunityMember(Community community, User user, CommunityRole role) {
        this.community = community;
        this.user = user;
        this.role = role;
        this.joinedAt = Instant.now(); // or null
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunityMember)) return false;
        CommunityMember that = (CommunityMember) o;
        return user != null && community != null &&
                user.getId() != null && community.getId() != null &&
                user.getId().equals(that.user.getId()) &&
                community.getId().equals(that.community.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                user != null ? user.getId() : 0L,
                community != null ? community.getId() : 0L
        );
    }
}
