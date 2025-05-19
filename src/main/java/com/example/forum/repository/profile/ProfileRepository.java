package com.example.forum.repository.profile;

import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);
    Profile findByPublicId(String publicId);
}
