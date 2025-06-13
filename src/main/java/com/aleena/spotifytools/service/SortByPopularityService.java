package com.aleena.spotifytools.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;


@Service
public class SortByPopularityService {
    public String sortByPop(SpotifyApi spotifyApi) {
        String tracks =  "worked?";
        Integer offset = 0;
        Integer limit = 100;
        Boolean nextExists = true;
        String playlistLink = "23AEJGM7I3eVjbAR7gWOMt";

        Map<Integer, Integer> playlistVals = new HashMap<>();

        int currentIt = 0;

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
