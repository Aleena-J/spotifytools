package com.aleena.spotifytools.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aleena.spotifytools.entity.UserPlayedSong;
import com.aleena.spotifytools.entity.UserProfile;



public interface UserPlayedSongRepository extends JpaRepository<UserPlayedSong, Long> {
    @Query("SELECT ups FROM UserPlayedSong ups WHERE ups.user = :user AND ups.date = :date")
    List<UserPlayedSong> findSongsPlayedByUserAtTimestamp(@Param("user") UserProfile user, @Param("date") LocalDateTime date);



    @Query("SELECT ups FROM UserPlayedSong ups WHERE ups.user = :user")
    List<UserPlayedSong> findSongsPlayedByUser(@Param("user") UserProfile user);
}
