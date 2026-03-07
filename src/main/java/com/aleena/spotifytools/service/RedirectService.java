package com.aleena.spotifytools.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;


@Service
public class RedirectService {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserProfileRepository userProfileRepository;

    public String getDetails(String code, SpotifyApi spotifyApi) throws IOException {
        AuthorizationCodeRequest authCodeReq = spotifyApi.authorizationCode(code).build();
        String id = "noID";
        try{
            final AuthorizationCodeCredentials authCodeCreds = authCodeReq.execute();

            spotifyApi.setAccessToken(authCodeCreds.getAccessToken());
			spotifyApi.setRefreshToken(authCodeCreds.getRefreshToken());


            final GetCurrentUsersProfileRequest getProfileReq = spotifyApi.getCurrentUsersProfile().build();
            User user = getProfileReq.execute();
            id = user.getId();
            if(!userProfileRepository.existsByUserId(user.getId())){
                userProfileService.insertOrUpdateProfile(user.getId(), authCodeCreds.getAccessToken(), authCodeCreds.getRefreshToken());
            }
        }catch(Exception e) {
            System.out.println("Error: RedirectService --- " + e.getMessage());
        }
        return id;
    }
}
