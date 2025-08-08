package com.aleena.spotifytools.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserPlayedSong;
import com.aleena.spotifytools.entity.UserProfile;



public interface UserPlayedSongRepository extends JpaRepository<UserPlayedSong, Long> {
    @Query("SELECT ups FROM UserPlayedSong ups WHERE ups.user = :user AND ups.date BETWEEN :date1 AND :date2")
    List<UserPlayedSong> findSongsPlayedByUser(@Param("user") UserProfile user, @Param("date1") LocalDateTime date1, @Param("date2") LocalDateTime date2);

    boolean existsByUserAndSongAndDate(UserProfile user, Song song, LocalDateTime date);

    @Modifying
    @Query("DELETE FROM UserPlayedSong ups WHERE ups.date BETWEEN :date1 AND :date2")
    void deleteByDates(@Param("date1") LocalDateTime date1, @Param("date2") LocalDateTime date2);

    @Modifying
    @Query("DELETE FROM UserPlayedSong ups WHERE ups.user = :user")
    void deleteByUser(@Param("user") UserProfile user);
}
