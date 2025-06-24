package com.aleena.spotifytools.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;


@Service
public class GetRecentlyPlayedService {

    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    public String recentlyPlayed(SpotifyApi spotifyApi, String userId) {
        String songList = "";
        int count = 0;
        int maxTries = 1;

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            spotifyApi.setAccessToken(user.getAccessToken());
            spotifyApi.setRefreshToken(user.getRefreshToken());
        }else{
            return "false";
        }
        
        final GetCurrentUsersRecentlyPlayedTracksRequest getRecentTracksReq = spotifyApi.getCurrentUsersRecentlyPlayedTracks().after(new Date(1484811043508L)).limit(50).build();

        while(true){
            try {
                final PagingCursorbased<PlayHistory> playHistoryPagingCursorbased = getRecentTracksReq.execute();

                PlayHistory[] history = playHistoryPagingCursorbased.getItems();

                for(int i = 0; i < history.length; i++){
                    songList +=  history[i].getTrack().getName();
                    ArtistSimplified[] artists = history[i].getTrack().getArtists();
                    songList += " - ";
                    if(artists.length > 1){
                        for(int j = 0; j < artists.length; j++){
                            songList += artists[j].getName();
                            if(j != artists.length - 1){
                                songList += ", ";
                            }
                        }
                    }else{
                        songList += artists[0].getName();
                    }

                    songList += " " + history[i].getPlayedAt().toString() + "<br>";
                }
                return songList;
            } catch (Exception e) {
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
