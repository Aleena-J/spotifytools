package com.aleena.spotifytools.service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aleena.spotifytools.repository.UserPlayedSongRepository;


@Component
@Transactional
public class ClearSongsService {

    @Autowired
    UserPlayedSongRepository songUserPlayedRepository;


    //TODO: CHANGE TO MONTHLY WHEN NOT TESTING
    @Scheduled(fixedRate = 30000)
    public void clearSongs() throws IOException{
        LocalDateTime today = LocalDateTime.now();
        //month before last
        LocalDateTime startMonth = today.minusMonths(2).withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endMonth = today.minusMonths(2).withDayOfMonth(today.minusMonths(2).toLocalDate().lengthOfMonth()).with(LocalTime.MAX);

        songUserPlayedRepository.deleteByDates(startMonth, endMonth);
    }
    
}
