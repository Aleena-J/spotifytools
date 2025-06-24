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


    @Value("${redirect.server.ip}")
    private String redirectUri;

    public SpotifyApi spotifyApi(){
        return new SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(java.net.URI.create(redirectUri))
        .build();
    }
}

