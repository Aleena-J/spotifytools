package com.aleena.spotifytools.entity;

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
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", columnDefinition = "TEXT", nullable = false)
    private String songName;

    @Column(name = "ARTISTS", columnDefinition = "TEXT", nullable = false)
    private String artists;

    @Column(name = "SONG_ID", columnDefinition = "TEXT", nullable = false, unique = true)
    private String songId;
    
    public Song() {}

    public Song(String songName, String artists, String songId){
        this.songName = songName;
        this.artists = artists;
        this.songId = songId;
    }

}