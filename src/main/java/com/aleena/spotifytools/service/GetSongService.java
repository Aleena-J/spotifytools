package com.aleena.spotifytools.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

// public class GetSongService {
//     @Scheduled(fixedRate = 5000)
//     public void getSong() throws IOException{
//         if(user != null){
//             SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
//             final GetInformationAboutUsersCurrentPlaybackRequest getInfo = spotifyApi.getInformationAboutUsersCurrentPlayback().build();
//             try{
//                 final CurrentlyPlayingContext context = getInfo.execute();
//                 if(context.getIs_playing()){
//                     final GetUsersCurrentlyPlayingTrackRequest currentTrack = spotifyApi.getUsersCurrentlyPlayingTrack().build();
//                     final CurrentlyPlaying currentlyPlaying = currentTrack.execute();
//                     if(!songUri.equals(currentlyPlaying.getItem().getId())){
//                         songUri = currentlyPlaying.getItem().getId();
//                         Track song = (Track)currentlyPlaying.getItem();
//                         String artists = "";
//                         if(song.getArtists().length > 1){
//                             for(int i = 0; i < song.getArtists().length; i++){
//                                 artists += song.getArtists()[i].getName();
//                                 if(i != song.getArtists().length - 1){
//                                     artists += ", ";
//                                 }
//                             }
//                         }else{
//                             artists += song.getArtists()[0].getName();
//                         }
//                         trackedSongService.insertSong(songUri, currentlyPlaying.getItem().getName(), artists, LocalDateTime.now());
//                     }
//                 }
//             }catch(Exception e){
//                 System.out.println("Error: " + e.getMessage());
//             }

//         }
//     }

// }
