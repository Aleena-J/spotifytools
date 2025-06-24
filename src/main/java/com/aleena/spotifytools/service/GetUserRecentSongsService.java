package com.aleena.spotifytools.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aleena.spotifytools.repository.SongRepository;
import com.aleena.spotifytools.repository.UserPlayedSongRepository;
import com.aleena.spotifytools.repository.UserProfileRepository;
import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.entity.UserProfile;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

@Component
public class GetUserRecentSongsService {

    @Autowired
    UserProfileRepository profileRepository;

    @Autowired
    UserPlayedSongRepository songUserPlayedRepository;

    @Autowired
    UserPlayedSongService songUserPlayedService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    SongRepository songRepository;

    @Autowired
    SongService songService;

    @Autowired
    SpotifyConfig spotifyConfig;

    @Scheduled(fixedRate = 30000) //every 30s get all users' recently played and store new songs
    public void getSong() throws IOException{
        int maxTries = 1;
        List<UserProfile> userList =  profileRepository.findAll();
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        for(int i = 0; i < userList.size(); i++){
            int count = 0;
            spotifyApi.setAccessToken(userList.get(i).getAccessToken());
            spotifyApi.setRefreshToken(userList.get(i).getRefreshToken());

            final GetCurrentUsersRecentlyPlayedTracksRequest getRecentTracksReq = spotifyApi.getCurrentUsersRecentlyPlayedTracks().after(new Date(1484811043508L)).limit(50).build();
            while(true){
                try {
                    final PagingCursorbased<PlayHistory> playHistoryPagingCursorbased = getRecentTracksReq.execute();

                    PlayHistory[] history = playHistoryPagingCursorbased.getItems();

                    for(PlayHistory item : history){
                        String songId = item.getTrack().getId();
                        String songName = item.getTrack().getName();
                        String artists = "";
                        for(ArtistSimplified artist : item.getTrack().getArtists()){
                            artists += artist.getName() + ", ";
                        }
                        artists = artists.substring(0, artists.length()-2);
                        if(!songRepository.existsBySongId(songId)){
                            songService.insertSong(songName, artists, songId);
                        }

                        LocalDateTime datePlayed = LocalDateTime.ofInstant(item.getPlayedAt().toInstant(), ZoneId.systemDefault());
                        songUserPlayedService.insertSong(userList.get(i), songRepository.findBySongId(songId), datePlayed);
                    }
                    break;
                }catch (Exception e) {
                    if(count == maxTries){
                        break;
                    }else{
                        try{
                            final AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
                            final AuthorizationCodeCredentials creds = authCodeRefreshReq.execute();
                            spotifyApi.setAccessToken(creds.getAccessToken());
                            userProfileService.insertOrUpdateProfile(userList.get(i).getUserId(), creds.getAccessToken(), spotifyApi.getRefreshToken());
                            count++;
                        }catch(Exception er){
                            break;
                        }
                    }
                }       
            }
        }
    }
}


