package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.repository.SongRepository;

@Service
public class SongService {
    @Autowired
    SongRepository songRepository;

    public void insertSong(String songName, String artists, String songId, String imageUrl, String album, String spotifyLink){
        Song song = new Song(songName, artists, songId, imageUrl, album, spotifyLink);
        songRepository.save(song);
    }
}
