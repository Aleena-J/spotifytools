package com.aleena.spotifytools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.player.AddItemToUsersPlaybackQueueRequest;

@Service
public class AddToQueueService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    public String addToQueue(SpotifyApi spotifyApi, String userId, String uri){
        int refreshAttempts = 0;
        final int maxRetries = 3;
        UserProfile user = profileRepository.findByUserId(userId);
        spotifyApi.setAccessToken(user.getAccessToken());
        spotifyApi.setRefreshToken(user.getRefreshToken());

        if(profileRepository.existsByUserId(userId)){
            while(true){
                try{
                    String trackUri = "spotify:track:" + uri;
                    AddItemToUsersPlaybackQueueRequest addReq = spotifyApi.addItemToUsersPlaybackQueue(trackUri).build();
                    String res = addReq.execute();
                    return "Successfully added";
                } catch (TooManyRequestsException e) {
                    int retryTime = e.getRetryAfter();
                    try {
                        System.out.println("Rate limited: Waiting " + retryTime + "s");
                        Thread.sleep(retryTime * 1000L);
                        continue;
                    } catch (InterruptedException | NumberFormatException ex) {
                        System.err.println("Invalid Retry-After. Waiting 5s");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ignore) {}
                        continue;
                    }
                } catch(Exception e){
                    if(refreshAttempts == maxRetries){
                        System.out.println(e.getMessage());
                        return ("Adding unsuccessful");
                    }else{
                        try{
                            final AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
                            final AuthorizationCodeCredentials creds = authCodeRefreshReq.execute();
                            spotifyApi.setAccessToken(creds.getAccessToken());
                            userProfileService.insertOrUpdateProfile(userId, creds.getAccessToken(), spotifyApi.getRefreshToken());
                            refreshAttempts++;
                            continue;
                        }catch(Exception er){
                            System.out.println(er.getMessage());
                            return ("Adding unsuccessful");
                        }
                    }
                }
            }
        }else{
            return "Adding unsuccessful";
        }
    }
}
