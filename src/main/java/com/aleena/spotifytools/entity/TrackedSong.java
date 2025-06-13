package com.aleena.spotifytools.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;




@Entity
@Table(name = "SONGS")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TrackedSong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SONG_ID")
    private String songId;

    @Column(name = "SONG")
    private String songName;

    @Column(name = "ARTISTS")
    private String artists;

    @Column(name = "TIME_PLAYED")
    private LocalDateTime date;

    public TrackedSong(String songId, String songName, String artists, LocalDateTime date){
        this.songId = songId;
        this.songName = songName;
        this.artists = artists;
        this.date = date;
    }
}
