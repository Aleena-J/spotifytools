package com.aleena.spotifytools.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;


@Service
public class SortByPopularityService {

    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    public String sortByPop(SpotifyApi spotifyApi, String userId) {
        String tracks =  "empty";
        Integer offset = 0;
        Integer limit = 100;
        Boolean nextExists = true;
        //TODO: Edit to not be hardcoded, take in frontend link and check for playlist length, need frontend
        String playlistLink = "23AEJGM7I3eVjbAR7gWOMt";

        Map<Integer, Integer> playlistVals = new HashMap<>();

        int currentIt = 0;
        int count = 0;
        int maxTries = 1;

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            spotifyApi.setAccessToken(user.getAccessToken());
            spotifyApi.setRefreshToken(user.getRefreshToken());
        }else{
            return "Sorting unsuccessful";
        }


        //get next batch of tracks if needed, then each tracks URI and popularity
        while(true){
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
                    if(tracks.equals("empty")){
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
                
                return "Sorting successful";
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
