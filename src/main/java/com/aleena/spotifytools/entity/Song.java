package com.aleena.spotifytools.entity;

import java.io.Serializable;

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
public class Song implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", columnDefinition = "TEXT", nullable = false)
    private String songName;

    @Column(name = "ARTISTS", columnDefinition = "TEXT", nullable = false)
    private String artists;

    @Column(name = "SONG_ID", columnDefinition = "TEXT", nullable = false, unique = true)
    private String songId;

    @Column(name = "IMAGE_URL", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "ALBUM", columnDefinition = "TEXT", nullable = false)
    private String album;

    @Column(name = "SPOTIFY_LINK", columnDefinition = "TEXT", nullable = false, unique = true)
    private String spotifyLink;
    
    public Song() {}

    public Song(String songName, String artists, String songId, String imageUrl, String album, String spotifyLink){
        this.songName = songName;
        this.artists = artists;
        this.songId = songId;
        this.imageUrl = imageUrl;
        this.album = album;
        this.spotifyLink = spotifyLink;
    }

}