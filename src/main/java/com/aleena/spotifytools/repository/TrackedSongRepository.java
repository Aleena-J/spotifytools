package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.TrackedSong;


public interface TrackedSongRepository extends JpaRepository<TrackedSong, Long> {
    TrackedSong findBySongId(String songId);
}
