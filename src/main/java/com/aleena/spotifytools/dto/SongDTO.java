package com.aleena.spotifytools.dto;

import lombok.Data;

@Data
public class SongDTO {
    private String songName;
    private String artists;
    private String songId;
    private int repeats;

    public SongDTO(String songName, String artists, String songId, int repeats) {
        this.songName = songName;
        this.artists = artists;
        this.songId = songId;
        this.repeats = repeats;
    }
}
