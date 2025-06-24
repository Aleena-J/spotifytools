package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.UserSkippedSong;

public interface UserSkippedSongRepository extends JpaRepository<UserSkippedSong, Long>{
}
