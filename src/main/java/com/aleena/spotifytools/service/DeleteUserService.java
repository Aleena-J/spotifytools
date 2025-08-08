package com.aleena.spotifytools.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserPlayedSongRepository;
import com.aleena.spotifytools.repository.UserProfileRepository;

@Transactional
@Service
public class DeleteUserService {
    @Autowired
    UserProfileRepository profileRepository;

    @Autowired
    UserPlayedSongRepository songUserPlayedRepository;

    @Autowired
    UserProfileService userProfileService;

    public String deleteUser(String userId) throws IOException{
        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);

            songUserPlayedRepository.deleteByUser(user);

            profileRepository.deleteByUserId(userId);

            return "Deletion successful";
        }else{
            return "User not found";
        }
    }
}
