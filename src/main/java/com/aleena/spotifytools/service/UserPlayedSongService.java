package com.aleena.spotifytools.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserPlayedSong;
import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserPlayedSongRepository;

@Service
public class UserPlayedSongService {
    @Autowired
    private UserPlayedSongRepository playedSongRepository;


    public void insertSong(UserProfile id, Song name, LocalDateTime date){
        UserPlayedSong song = new UserPlayedSong(id, name, date);
        playedSongRepository.save(song);
    }
    
}
