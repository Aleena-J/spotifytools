package com.aleena.spotifytools.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.dto.SongDTO;
import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;

@Service
public class CreateRepeatPlaylistService {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;
    
    public String createRepeatPlaylist(SpotifyApi spotifyApi, String userId, String period, List<SongDTO> songs){
        int refreshAttempts = 0;
        final int maxRetries = 3;
        UserProfile user = profileRepository.findByUserId(userId);
        spotifyApi.setAccessToken(user.getAccessToken());
        spotifyApi.setRefreshToken(user.getRefreshToken());

        if(profileRepository.existsByUserId(userId)){
            while(true){
                try{
                    LocalDate today = LocalDate.now();
                    String playlistName = "Your repeats for ";
                    if(period.equals("today")){
                        playlistName += "the day " + today.toString();
                    }else if(period.equals("week")){
                        LocalDate startWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                        playlistName += "the week of " + startWeek.toString();
                    }else if(period.equals("month")){
                        LocalDate startMonth = today.withDayOfMonth(1);
                        playlistName += "the month of " + startMonth.toString();
                    }else if(period.equals("lastmonth")){
                        LocalDate startLastMonth = today.minusMonths(1).withDayOfMonth(1);
                        playlistName += "the month of " + startLastMonth.toString();
                    }

                    String playlistDesc = " ";
                    Boolean playlistPublic = false;
                    Boolean playlistCollab = false;
                    CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, playlistName)
                    .description(playlistDesc)
                    .public_(playlistPublic)
                    .collaborative(playlistCollab)
                    .build();
                    Playlist newPlaylist = createPlaylistRequest.execute();

                    List<JsonObject> uriBatches = new ArrayList<>();
                    int maxBatchSize = 100;
                    int numIts = Math.ceilDiv(songs.size(), maxBatchSize);
                    int index = 0; //track position in playlist

                    for(int i = 0; i < numIts; i++){
                        int tracksToTake = Math.min(maxBatchSize, songs.size()-index);
                        JsonArray uriArray = new JsonArray();
                        for(int j = 0; j < tracksToTake; j++){
                            uriArray.add("spotify:track:" + songs.get(index).getSongId());
                            index++;
                        }
                        JsonObject batch = new JsonObject();
                        batch.add("uris", uriArray);
                        uriBatches.add(batch);
                    }

                    for(int i = 0; i < numIts; i++){
                        AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(newPlaylist.getId(), uriBatches.get(i).getAsJsonArray("uris")).build();
                        SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
                        System.out.println(snapshotResult);
                    }

                    return "Creation successful";
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
                        return ("Creation unsuccessful");
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
                            return ("Creation unsuccessful");
                        }
                    }
                }
            }
        }else{
            return "Creation unsuccessful";
        }
    }
}
