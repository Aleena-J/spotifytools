package com.aleena.spotifytools.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aleena.spotifytools.repository.SongRepository;
import com.aleena.spotifytools.repository.UserPlayedSongRepository;
import com.aleena.spotifytools.repository.UserProfileRepository;
import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.entity.Song;
import com.aleena.spotifytools.entity.UserProfile;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
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

    //TODO: CHANGE TO 24 MINS WHEN NOT TESTING
    @Scheduled(fixedRate = 30000) //every 24min get all users' recently played and store new songs
    public void getSong() throws IOException{
        int maxTries = 3;
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
                        String imageUrl = item.getTrack().getAlbum().getImages()[0].getUrl();
                        String albumName = item.getTrack().getAlbum().getName();
                        String songLink = item.getTrack().getExternalUrls().get("spotify");
                        String songName = item.getTrack().getName();
                        String artists = "";
                        for(ArtistSimplified artist : item.getTrack().getArtists()){
                            artists += artist.getName() + ", ";
                        }
                        artists = artists.substring(0, artists.length()-2);
                        if(!songRepository.existsBySongId(songId)){
                            songService.insertSong(songName, artists, songId, imageUrl, albumName, songLink);
                        }

                        Song song = songRepository.findBySongId(songId);

                        LocalDateTime datePlayed = LocalDateTime.ofInstant(item.getPlayedAt().toInstant(), ZoneOffset.UTC);

                        Set<String> inserted = new HashSet<>();
                        
                        String songKey = userList.get(i).getUserId() + "-" + songId + "-" + datePlayed.toString();

                        if (!inserted.contains(songKey) && !songUserPlayedRepository.existsByUserAndSongAndDate(userList.get(i), song, datePlayed)) {
                            songUserPlayedService.insertSong(userList.get(i), song, datePlayed);
                            inserted.add(songKey);
                        }
                    }
                    break;
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
                } catch (Exception e) {
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