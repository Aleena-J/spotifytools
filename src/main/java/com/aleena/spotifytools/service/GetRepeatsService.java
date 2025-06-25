package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.entity.UserPlayedSong;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aleena.spotifytools.repository.UserPlayedSongRepository;
import com.aleena.spotifytools.repository.UserProfileRepository;



@Service
public class GetRepeatsService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;
    @Autowired
    UserPlayedSongRepository playedSongRepository;
    //TODO: Differentiate bt daily, monthly, weekly, frontend needed
    public String repeats(String userId){

        //daily
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(LocalTime.MIN);
        LocalDateTime end = today.with(LocalTime.MAX);
        Map<String, Integer> songVals = new HashMap<>();

        String test = "";

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            List<UserPlayedSong> songs = playedSongRepository.findSongsPlayedByUser(user, start, end);
            for(UserPlayedSong song : songs){
                if(songVals.keySet().contains(song.getSong().getSongId()+"@"+song.getSong().getSongName())){
                    songVals.put(song.getSong().getSongId()+"@"+song.getSong().getSongName(), songVals.get(song.getSong().getSongId()+"@"+song.getSong().getSongName()) + 1);
                }else{
                    songVals.put(song.getSong().getSongId()+"@"+song.getSong().getSongName(), 1);
                }
            }

            for(Map.Entry<String, Integer> entry : songVals.entrySet()){
                if(entry.getValue() >= 1){
                    String[] keyParts = entry.getKey().split("@");
                    test += keyParts[1] + " --- " + entry.getValue().toString() + "<br>";
                }
            }
        }else{
            return "Error: could not find user";
        }
        return test;
    }
}
