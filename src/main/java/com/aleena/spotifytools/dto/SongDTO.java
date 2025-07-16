package com.aleena.spotifytools.dto;

import lombok.Data;

@Data
public class SongDTO {
    private String songName;
    private String artists;
    private String songId;
    private int repeats;
    private String dateType;
    private String imageUrl;
    private String album;
    private String songUrl;

    public SongDTO(String songName, String artists, String songId, int repeats, String dateType, String imageUrl, String album, String songUrl) {
        this.songName = songName;
        this.artists = artists;
        this.songId = songId;
        this.repeats = repeats;
        this.dateType = dateType;
        this.imageUrl = imageUrl;
        this.album = album;
        this.songUrl = songUrl;
    }
}
