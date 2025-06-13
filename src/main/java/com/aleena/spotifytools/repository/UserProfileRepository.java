package com.aleena.spotifytools.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aleena.spotifytools.entity.UserProfile;



public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUserId(String userId);
    boolean existsByUserId(String userId);
}
