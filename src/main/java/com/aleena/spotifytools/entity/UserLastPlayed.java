package com.aleena.spotifytools.entity;

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

@Entity
@Table(name = "LAST_PLAYED")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserLastPlayed {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER", nullable = false)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG", nullable = false)
    private Song song;

    @Column(name = "TIME_ELAPSED", nullable = false)
    private Integer time;

    public UserLastPlayed(){}

    public UserLastPlayed(UserProfile user, Song song, Integer timeMS){
        this.user = user;
        this.song = song;
        this.time = timeMS;
    }
}
