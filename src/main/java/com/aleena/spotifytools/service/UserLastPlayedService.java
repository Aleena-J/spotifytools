package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserLastPlayed;
import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserLastPlayedRepository;

@Service
public class UserLastPlayedService {
    @Autowired
    private UserLastPlayedRepository lastPlayedRepository;

    public void insertOrUpdateProfile(UserProfile user, Song song, Integer timeMS){
        UserLastPlayed lastPlayed;
        if(lastPlayedRepository.existsByUser(user)){
            lastPlayed = lastPlayedRepository.findByUser(user);
        }else{
            lastPlayed = null;
        }
        if(lastPlayed != null){
            lastPlayed.setSong(song);
            lastPlayed.setTime(timeMS);
        }else{
            lastPlayed = new UserLastPlayed(user, song, timeMS);
        }
        lastPlayedRepository.save(lastPlayed);
    }
}
