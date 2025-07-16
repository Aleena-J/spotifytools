package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.dto.SongDTO;
import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserPlayedSong;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
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

    public void getSongsFromDate(LocalDateTime start, LocalDateTime end, UserProfile user, List<SongDTO> repeatedSongs, String dateType){
        List<UserPlayedSong> songs = playedSongRepository.findSongsPlayedByUser(user, start, end);

        Map<Song, Integer> songValues = new HashMap<>();

        for(UserPlayedSong song : songs){
            if(songValues.keySet().contains(song.getSong())){
                songValues.put(song.getSong(), songValues.get(song.getSong()) + 1);
            }else{
                songValues.put(song.getSong(), 1);
            }
        }
        
        for (Map.Entry<Song, Integer> entry : songValues.entrySet()) {
            Song song = entry.getKey();
            int count = entry.getValue();
            if (count > 1) {
                repeatedSongs.add(new SongDTO(song.getSongName(), song.getArtists(), song.getSongId(), count, dateType, song.getImageUrl(), song.getAlbum(), song.getSpotifyLink()));
            }
        }
    }


    public List<SongDTO> repeats(String userId){
        List<SongDTO> repeatedSongs = new ArrayList<>();
        
        LocalDateTime today = LocalDateTime.now();
        //day
        LocalDateTime startDay = today.with(LocalTime.MIN);
        LocalDateTime endDay = today.with(LocalTime.MAX);

        //week
        LocalDateTime startWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MIN);
        LocalDateTime endWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).with(LocalTime.MAX);

        //month
        LocalDateTime startMonth = today.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endMonth = today.withDayOfMonth(today.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);

        //last month
        LocalDateTime startLastMonth = today.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endLastMonth = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).toLocalDate().lengthOfMonth()).with(LocalTime.MAX);

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            getSongsFromDate(startDay, endDay, user, repeatedSongs, "today");
            getSongsFromDate(startWeek, endWeek, user, repeatedSongs, "week");
            getSongsFromDate(startMonth, endMonth, user, repeatedSongs, "month");
            getSongsFromDate(startLastMonth, endLastMonth, user, repeatedSongs, "lastmonth");
        }else{
            return Collections.emptyList();
        }
        return repeatedSongs;
    }
}
