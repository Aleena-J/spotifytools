package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.TrackedSong;
import com.aleena.spotifytools.repository.TrackedSongRepository;

public class TrackedSongService {
    @Autowired
    private TrackedSongRepository trackedSongRepository;


    public void insertSong(String id){
        
    }
    
}
