package com.aleena.spotifytools.dto;
import lombok.Data;

@Data
public class PlaylistDTO {
    private String playlistID;
    private String imageUrl;
    private String playlistName;
    private int numTracks;

    public PlaylistDTO(String playlistID, String imageUrl, String playlistName, int numTracks) {
        this.playlistID = playlistID;
        this.imageUrl = imageUrl;
        this.playlistName = playlistName;
        this.numTracks = numTracks;
    }
}
