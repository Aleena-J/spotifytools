package com.aleena.spotifytools.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.*;

import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.repository.UserProfileRepository;
import com.aleena.spotifytools.service.UserProfileService;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;





@RestController
public class SpotifytoolsController {
    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private UserProfileService userProfileService;

    private String code = "";

    private String songUri = "";

    private User user = null;

    @Scheduled(fixedRate = 5000)
    public void getSong(){
        if(user != null){
            System.out.println("working");
        }
    }

    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/login")
    @ResponseBody
    public void spotifyLogin(HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        AuthorizationCodeUriRequest authCodeUriReq = spotifyApi.authorizationCodeUri()
        .scope("user-read-recently-played, user-read-currently-playing, playlist-read-private, playlist-modify-public, playlist-modify-private, user-read-email")
        .show_dialog(true)
        .build();

        final URI uri = authCodeUriReq.execute();
        response.sendRedirect(uri.toString());
    }

    @GetMapping("/callback")
    public void getMethodName(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        code = userCode;
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        AuthorizationCodeRequest authCodeReq = spotifyApi.authorizationCode(code).build();

        try{
            final AuthorizationCodeCredentials authCodeCreds = authCodeReq.execute();

            spotifyApi.setAccessToken(authCodeCreds.getAccessToken());
			spotifyApi.setRefreshToken(authCodeCreds.getRefreshToken());


            final GetCurrentUsersProfileRequest getProfileReq = spotifyApi.getCurrentUsersProfile().build();
            user = getProfileReq.execute();
            userProfileService.insertOrUpdateProfile(user.getId());

        }catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        response.sendRedirect("get-recently-played");
    }
    

    @GetMapping("get-recently-played")
    public String recentlyPlayed() {
        String songList = "";
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();

        final GetCurrentUsersRecentlyPlayedTracksRequest getRecentTracksReq = spotifyApi.getCurrentUsersRecentlyPlayedTracks().after(new Date(1484811043508L)).limit(50).build();

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
            return ("Error :" + e.getMessage());
        }
    }


    @GetMapping("sort-playlist-popularity")
    public String postMethodName() {
        String tracks =  "worked?";
        Integer offset = 0;
        Integer limit = 100;
        Boolean nextExists = true;
        String playlistLink = "23AEJGM7I3eVjbAR7gWOMt";

        Map<Integer, Integer> playlistVals = new HashMap<>();

        int currentIt = 0;
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();

        //get next batch of tracks if needed, then each tracks URI and popularity
        try{
            while(nextExists){
                final GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistLink).limit(limit).offset(offset).build();
                final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

                if(playlistTrackPaging.getNext() != null){
                    //https://stackoverflow.com/questions/63308487/how-do-i-get-parameter-value-from-url-as-string
                    String[] splitUrlOffset = playlistTrackPaging.getNext().split("offset=");
                    offset = Integer.valueOf(splitUrlOffset[1].substring(0, splitUrlOffset[1].indexOf("&")));
                    String[] splitUrlLimit = splitUrlOffset[1].split("limit=");
                    limit = Integer.valueOf(splitUrlLimit[1]);
                }else{
                    nextExists = false;
                }

                PlaylistTrack[] items = playlistTrackPaging.getItems();

                for(int i = 0; i < items.length; i++){
                    Track track = (Track)items[i].getTrack();
                    playlistVals.put(i + currentIt, track.getPopularity());
                }
                currentIt += 100;
            }

            //sort map by popularity
            //https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
            Map<Integer, Integer> sorted = playlistVals
            .entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            
            int insert = 0;

            for(Map.Entry<Integer, Integer> entry : sorted.entrySet()){
                if(tracks.equals("worked?")){
                    System.out.println(entry.getKey() + " " + insert);
                    final ReorderPlaylistsItemsRequest reorderReq = spotifyApi.reorderPlaylistsItems(playlistLink, entry.getKey(), insert).build();
                    final SnapshotResult res = reorderReq.execute();
                    tracks = res.getSnapshotId();
                }else{
                    System.out.println(entry.getKey() + " " + insert);
                    final ReorderPlaylistsItemsRequest reorderReq = spotifyApi.reorderPlaylistsItems(playlistLink, entry.getKey(), insert).snapshot_id(tracks).build();
                    final SnapshotResult res = reorderReq.execute();
                    tracks = res.getSnapshotId();
                }
                insert++;
            }
            
            return tracks;
        }catch(Exception e){
            return ("Error :" + e.getMessage());
        }
    }
    
}
