package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;

@Service
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;


    public void insertOrUpdateProfile(String id, String access, String refresh){
        UserProfile userProfile = new UserProfile(id, access, refresh);
        userProfileRepository.save(userProfile);
    }
}
