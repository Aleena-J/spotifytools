package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.dto.SongDTO;
import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserPlayedSong;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<SongDTO> repeats(String userId){
        List<SongDTO> repeatedSongs = new ArrayList<>();
        //daily
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start = today.with(LocalTime.MIN);
        LocalDateTime end = today.with(LocalTime.MAX);
        Map<Song, Integer> songVals = new HashMap<>();

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            List<UserPlayedSong> songs = playedSongRepository.findSongsPlayedByUser(user, start, end);
            for(UserPlayedSong song : songs){
                if(songVals.keySet().contains(song.getSong())){
                    songVals.put(song.getSong(), songVals.get(song.getSong()) + 1);
                }else{
                    songVals.put(song.getSong(), 1);
                }
            }
            
            for (Map.Entry<Song, Integer> entry : songVals.entrySet()) {
                Song song = entry.getKey();
                int count = entry.getValue();
                if (count > 1) {
                    repeatedSongs.add(new SongDTO(song.getSongName(), song.getArtists(), song.getSongId(), count));
                }
            }

        }else{
            System.out.println("----- no id found -----");
            return Collections.emptyList();
        }
        return repeatedSongs;
    }
}
