package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.Song;


public interface SongRepository extends JpaRepository<Song, Long> {
    boolean existsBySongId(String songId);
    Song findBySongId(String songId);
}
