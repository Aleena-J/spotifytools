package com.aleena.spotifytools.service;

import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;

@Service
public class SkippedSongsTrackService {
    public String trackSkip(SpotifyApi spotifyApi, String userId){
        //check if listening to anything
            //if listening -> get song, progress
            //check if playing song is the same as stored
            //if same, check and store elapsed
            //if different, check stored songs elapsed and track skipped if elapsed <50% , then store current song

            //not listening
            //clear tracked song if elapsed is high enough, otherwise keep and wait unntil playing again





        //repeat tracks -> check elapsed, compare > or <
        
        
        return "";
    }
}
