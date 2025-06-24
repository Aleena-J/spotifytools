package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.UserPlayedSong;



public interface UserPlayedSongRepository extends JpaRepository<UserPlayedSong, Long> {
}
