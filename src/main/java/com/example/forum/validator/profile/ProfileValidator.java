package com.example.forum.validator.profile;

import com.example.forum.exception.profile.ProfileNotFoundException;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileValidator {

    private final ProfileRepository profileRepository;

    public Profile getProfileByUser(User user) {
        return profileRepository.findByUser(user)
                .orElseThrow(ProfileNotFoundException::new);
    }
}
