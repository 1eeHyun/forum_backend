package com.example.forum.repository.community;

import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityFavorite;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityFavoriteRepository extends JpaRepository<CommunityFavorite, Long> {

    Optional<CommunityFavorite> findByUserAndCommunity(User user, Community community);
    List<CommunityFavorite> findAllByUser(User user);
    boolean existsByUserAndCommunity(User user, Community community);
    void deleteByUserAndCommunity(User user, Community community);
}
