package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

@Service
public class SkippedSongsTrackService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    public String trackSkip(SpotifyApi spotifyApi, String userId){
        int count = 0;
        int maxTries = 1;

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            spotifyApi.setAccessToken(user.getAccessToken());
            spotifyApi.setRefreshToken(user.getRefreshToken());
        }else{
            return "Error: Unable to find user";
        }
        //check if listening to anything

        final GetUsersCurrentlyPlayingTrackRequest trackRequest = spotifyApi.getUsersCurrentlyPlayingTrack().build();

        while(true){
            try{
                final CurrentlyPlaying currentlyPlaying = trackRequest.execute();

                if(currentlyPlaying.getIs_playing()){
                    //if listening -> get song, progress
                    //check if playing song is the same as stored
                    //if same, check and store elapsed
                    //if different, check stored songs elapsed and track skipped if elapsed <50% , then store current song

                }else{
                    //not listening
                    //clear tracked song if elapsed is high enough, otherwise keep and wait unntil playing again

                }
                //repeat tracks -> check elapsed, compare > or <
                return "";
            }catch(Exception e){
                if(count == maxTries){
                    return ("Error :" + e.getMessage());
                }else{
                    try{
                        final AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
                        final AuthorizationCodeCredentials creds = authCodeRefreshReq.execute();
                        spotifyApi.setAccessToken(creds.getAccessToken());
                        userProfileService.insertOrUpdateProfile(userId, creds.getAccessToken(), spotifyApi.getRefreshToken());
                        count++;
                    }catch(Exception er){
                        return ("Error :" + er.getMessage());
                    }
                }
            }
        }
    }
}
