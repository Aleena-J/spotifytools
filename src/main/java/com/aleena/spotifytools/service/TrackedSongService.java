package com.aleena.spotifytools.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.TrackedSong;
import com.aleena.spotifytools.repository.TrackedSongRepository;

@Service
public class TrackedSongService {
    @Autowired
    private TrackedSongRepository trackedSongRepository;


    public void insertSong(String id, String name, String artists, LocalDateTime date){
        TrackedSong song = new TrackedSong(id, name, artists, date);
    }
    
}
