package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.UserLastPlayed;
import com.aleena.spotifytools.entity.UserProfile;


public interface UserLastPlayedRepository extends JpaRepository<UserLastPlayed, Long> {
    UserLastPlayed findByUser(UserProfile user);
    boolean existsByUser(UserProfile user);
}
