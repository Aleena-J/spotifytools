package com.aleena.spotifytools.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.UniqueConstraint;




@Entity
@Table(name = "SONGS_PLAYED", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"USER", "SONG", "DATE"})
})
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserPlayedSong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER", nullable = false)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG", nullable = false)
    private Song song;

    @Column(name = "TIME_PLAYED", nullable = false, updatable = false)
    private LocalDateTime date;

    public UserPlayedSong() {}

    public UserPlayedSong(UserProfile user, Song song, LocalDateTime date){
        this.date = date;
        this.user = user;
        this.song = song;
    }
}
