package com.aleena.spotifytools.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;


@Service
public class GetRecentlyPlayedService {
    public String recentlyPlayed(SpotifyApi spotifyApi) {
        String songList = "";
        
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
}
