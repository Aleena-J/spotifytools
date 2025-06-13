package com.aleena.spotifytools.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import se.michaelthelin.spotify.SpotifyApi;


@Configuration
public class SpotifyConfig {

    
    @Value("${spotify.clientId}")
    private String clientId;


    @Value("${spotify.clientSecret}")
    private String clientSecret;

    public SpotifyApi spotifyApi(){
        return new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(java.net.URI.create("http://127.0.0.1:8080/callback"))
        .build();
    }
}

