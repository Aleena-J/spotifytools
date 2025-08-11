package com.aleena.spotifytools.dto;
import java.util.List;

import lombok.Data;

@Data
public class PlaylistCreateDTO {
    private String period;
    private List<SongDTO> songs;

    public PlaylistCreateDTO(String period, List<SongDTO> songs){
        this.period = period;
        this.songs = songs;
    }
    
}
