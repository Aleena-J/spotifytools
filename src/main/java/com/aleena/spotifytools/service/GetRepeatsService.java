package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.entity.UserPlayedSong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aleena.spotifytools.repository.UserPlayedSongRepository;
import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;

@Service
public class GetRepeatsService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;
    @Autowired
    UserPlayedSongRepository playedSongRepository;

    Map<String, Integer> songVals = new HashMap<>();


    //TODO: Differentiate bt daily, monthly, weekly
    public String repeats(SpotifyApi spotifyApi, String userId){
        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            List<UserPlayedSong> songs = playedSongRepository.findSongsPlayedByUser(user);
            for(UserPlayedSong song : songs){
                if(songVals.keySet().contains(song.getSong().getSongName())){
                    songVals.put(song.getSong().getSongName(), songVals.get(song.getSong().getSongName()) + 1);
                }else{
                    songVals.put(song.getSong().getSongName(), 1);
                }
            }


            for(Map.Entry<String, Integer> entry : songVals.entrySet()){
                if(entry.getValue() > 3){
                    System.out.println(entry.getKey());
                }
            }
        }else{
            return "false";
        }
        return "true";
    }
}
