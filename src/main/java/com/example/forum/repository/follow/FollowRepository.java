package com.example.forum.repository.follow;

import com.example.forum.model.follow.Follow;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);

    List<Follow> findAllByFollower(User follower);
    List<Follow> findAllByFollowing(User following);
}
