package com.aleena.spotifytools.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aleena.spotifytools.entity.UserProfile;
import com.aleena.spotifytools.repository.UserProfileRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.RemoveItemsFromPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReorderPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.playlists.ReplacePlaylistsItemsRequest;


@Service
public class SortByPopularityService {

    @Autowired
    UserProfileService userProfileService;
    @Autowired
    UserProfileRepository profileRepository;

    //String method
    public String sortByPop(SpotifyApi spotifyApi, String userId, String playlistLink, String method) {
        String snapshotId = "empty"; 
        Integer offset = 0;
        Integer limit = 100;
        Boolean nextExists = true;
        

        int refreshAttempts = 0;
        final int maxRetries = 3;

        if(profileRepository.existsByUserId(userId)){
            UserProfile user = profileRepository.findByUserId(userId);
            spotifyApi.setAccessToken(user.getAccessToken());
            spotifyApi.setRefreshToken(user.getRefreshToken());
        }else{
            return "Sorting unsuccessful";
        }

        List<Track> trackList = new ArrayList<>();
        List<String> trackUris = new ArrayList<>();
        
        
        //get next batch of tracks if needed, then each tracks URI and popularity
        while(true){
            try{
                while(nextExists){
                    final GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistLink).limit(limit).offset(offset).build();
                    final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsItemsRequest.execute();

                    if(playlistTrackPaging.getNext() != null){
                        // //https://stackoverflow.com/questions/63308487/how-do-i-get-parameter-value-from-url-as-string
                        // String[] splitUrlOffset = playlistTrackPaging.getNext().split("offset=");
                        // offset = Integer.valueOf(splitUrlOffset[1].substring(0, splitUrlOffset[1].indexOf("&")));
                        // String[] splitUrlLimit = splitUrlOffset[1].split("limit=");
                        // limit = Integer.valueOf(splitUrlLimit[1]);
                        offset += limit;
                    }else{
                        nextExists = false;
                    }

                    PlaylistTrack[] items = playlistTrackPaging.getItems();

                    for(int i = 0; i < items.length; i++){
                        Track track = (Track)items[i].getTrack();
                        trackList.add(track);
                        trackUris.add(track.getUri());
                    }
                }

                System.out.println("PLAYLIST LENGTH = " + trackList.size());
                //sort by popularity
                List<Track> sortedTracks = new ArrayList<>(trackList);
                    sortedTracks.sort((a, b) -> {
                        int popCompare = Integer.compare(b.getPopularity(), a.getPopularity());
                        if (popCompare != 0) return popCompare;

                        //artists alphabetical
                        String artistA = a.getArtists().length > 0 ? a.getArtists()[0].getName() : "";
                        String artistB = b.getArtists().length > 0 ? b.getArtists()[0].getName() : "";
                        return artistA.compareToIgnoreCase(artistB);
                    });

                if(method.equals("preserve")){
                    for (int i = 0; i < sortedTracks.size(); i++) {
                        Track targetTrack = sortedTracks.get(i);
                        int currentIndex = -1;


                        for (int j = 0; j < trackList.size(); j++) {
                            if (trackList.get(j).getUri().equals(targetTrack.getUri())) {
                                currentIndex = j;
                                break;
                            }
                        }
                        if (currentIndex == i) {
                            continue;
                        }

                        ReorderPlaylistsItemsRequest reorderReq;
                        if (snapshotId.equals("empty")) {
                            reorderReq = spotifyApi.reorderPlaylistsItems(playlistLink, currentIndex, i).build();
                        }else {
                            reorderReq = spotifyApi.reorderPlaylistsItems(playlistLink, currentIndex, i).snapshot_id(snapshotId).build();
                        }

                        SnapshotResult res = reorderReq.execute();
                        snapshotId = res.getSnapshotId();

                        Track moved = trackList.remove(currentIndex);
                        trackList.add(i, moved);

                        Thread.sleep(250);
                    }
                }else{
                    List<JsonObject> uriBatches = new ArrayList<>();
                    int maxBatchSize = 100;
                    int numIts = Math.ceilDiv(sortedTracks.size(), maxBatchSize);
                    int index = 0; //track position in playlist

                    for(int i = 0; i < numIts; i++){
                        int tracksToTake = Math.min(maxBatchSize, sortedTracks.size()-index);
                        JsonArray uriArray = new JsonArray();
                        for(int j = 0; j < tracksToTake; j++){
                            uriArray.add(sortedTracks.get(index).getUri());
                            index++;
                        }
                        JsonObject batch = new JsonObject();
                        batch.add("uris", uriArray);
                        uriBatches.add(batch);
                    }

                    if(method.equals("new")){
                        GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistLink).build();
                        Playlist playlist = getPlaylistRequest.execute();
                        String playlistName = playlist.getName() + " - sorted by popularity descending";
                        String playlistDesc = playlist.getDescription();
                        Boolean playlistPublic = playlist.getIsPublicAccess();
                        Boolean playlistCollab = playlist.getIsCollaborative();
                        CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, playlistName)
                        .description(playlistDesc)
                        .public_(playlistPublic)
                        .collaborative(playlistCollab)
                        .build();
                        Playlist newPlaylist = createPlaylistRequest.execute();

                        for(int i = 0; i < numIts; i++){
                            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(newPlaylist.getId(), uriBatches.get(i).getAsJsonArray("uris")).build();
                            SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
                            System.out.println(snapshotResult);
                        }


                    }else if(method.equals("overwrite")){
                        String[] filler = new String[]{"spotify:track:5XSKC4d0y0DfcGbvDOiL93"};
                        JsonArray fillerJson = JsonParser.parseString("[{\"uri\":\"spotify:track:5XSKC4d0y0DfcGbvDOiL93\"}]").getAsJsonArray();
                        ReplacePlaylistsItemsRequest replaceReq = spotifyApi.replacePlaylistsItems(playlistLink, filler).build();
                        replaceReq.execute();

                        RemoveItemsFromPlaylistRequest clearReq = spotifyApi.removeItemsFromPlaylist(playlistLink, fillerJson).build();
                        SnapshotResult res = clearReq.execute();
                        System.out.println(res);

                        for(int i = 0; i < numIts; i++){
                            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlistLink, uriBatches.get(i).getAsJsonArray("uris")).build();
                            SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
                            System.out.println(snapshotResult);
                        }
                    }
                }
                return "Sorting successful";
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
                    return ("Sorting unsuccessful");
                }else{
                    try{
                        final AuthorizationCodeRefreshRequest authCodeRefreshReq = spotifyApi.authorizationCodeRefresh().build();
                        final AuthorizationCodeCredentials creds = authCodeRefreshReq.execute();
                        spotifyApi.setAccessToken(creds.getAccessToken());
                        userProfileService.insertOrUpdateProfile(userId, creds.getAccessToken(), spotifyApi.getRefreshToken());
                        refreshAttempts++;
                        continue;
                    }catch(Exception er){
                        return ("Sorting unsuccessful");
                    }
                }
            }
        }
    }
}

