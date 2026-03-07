package com.aleena.spotifytools.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

import com.aleena.spotifytools.dto.PlaylistDTO;
import com.aleena.spotifytools.entity.UserProfile;;

@Service
public class GetUsersPlaylistsService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    public List<PlaylistDTO> getUsersPlaylists(SpotifyApi spotifyApi, String userId){
        Integer offset = 0;
        Integer limit = 50;
        Boolean nextExists = true;

        int refreshAttempts = 0;
        final int maxRetries = 3;

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            spotifyApi.setAccessToken(user.getAccessToken());
            spotifyApi.setRefreshToken(user.getRefreshToken());

            List<PlaylistDTO> playlists = new ArrayList<>();
            
            while(true){
                try{
                    while(nextExists){
                        final GetListOfUsersPlaylistsRequest getPlaylistsReq = spotifyApi.getListOfUsersPlaylists(userId)
                        .limit(limit)
                        .offset(offset)
                        .build();
                        final Paging<PlaylistSimplified> playlistPaging = getPlaylistsReq.execute();

                        if(playlistPaging.getNext() != null){
                            // String[] splitUrlOffset = playlistPaging.getNext().split("offset=");
                            // offset = Integer.valueOf(splitUrlOffset[1].substring(0, splitUrlOffset[1].indexOf("&")));
                            // String[] splitUrlLimit = splitUrlOffset[1].split("limit=");
                            // limit = Integer.valueOf(splitUrlLimit[1]);
                            offset += limit;
                        }else{
                            nextExists = false;
                        }

                        PlaylistSimplified[] items = playlistPaging.getItems();


                        for(int i = 0; i < items.length; i++){
                            String imageUrl = null;
                            if (items[i].getImages() != null && items[i].getImages().length > 0) {
                                imageUrl = items[i].getImages()[0].getUrl();
                            }
                            PlaylistDTO playlist = new PlaylistDTO(items[i].getId(), imageUrl, items[i].getName(), items[i].getTracks().getTotal(), items[i].getOwner().getDisplayName(), items[i].getExternalUrls().get("spotify"));
                            System.out.println(items[i].getName());
                            playlists.add(playlist);
                        }
                    }

                    return playlists;
                    
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
                    System.out.println(e.getMessage());
                    if(refreshAttempts == maxRetries){
                        return Collections.emptyList();
                    }else{
                        try{
                            final AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
                            final AuthorizationCodeCredentials creds = authCodeRefreshReq.execute();
                            spotifyApi.setAccessToken(creds.getAccessToken());
                            userProfileService.insertOrUpdateProfile(userId, creds.getAccessToken(), spotifyApi.getRefreshToken());
                            refreshAttempts++;
                            continue;
                        }catch(Exception er){
                            return Collections.emptyList();
                        }
                    }
                }
            }
        }else{
            return Collections.emptyList();
        }
    }
    
}
