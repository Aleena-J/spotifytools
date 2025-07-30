package com.aleena.spotifytools.dto;
import lombok.Data;

@Data
public class SortRequestDTO {
    private String playlistLink;
    private String method;

    public String getPlaylistLink() {
        return playlistLink;
    }
    public void setPlaylistLink(String playlistLink) {
        this.playlistLink = playlistLink;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
}


