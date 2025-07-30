package com.aleena.spotifytools.dto;
import lombok.Data;

@Data
public class PlaylistDTO {
    private String playlistID;
    private String imageUrl;
    private String playlistName;
    private int numTracks;
    private String owner;
    private String playlistUrl;

    public PlaylistDTO(String playlistID, String imageUrl, String playlistName, int numTracks, String owner, String playlistUrl) {
        this.playlistID = playlistID;
        this.imageUrl = imageUrl;
        this.playlistName = playlistName;
        this.numTracks = numTracks;
        this.owner = owner;
        this.playlistUrl = playlistUrl;
    }
}
