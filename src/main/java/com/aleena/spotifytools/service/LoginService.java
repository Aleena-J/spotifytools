package com.aleena.spotifytools.service;

import java.io.IOException;
import java.net.URI;

import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;


@Service
public class LoginService {
    public String spotifyLogin(SpotifyApi spotifyApi) throws IOException{
        AuthorizationCodeUriRequest authCodeUriReq = spotifyApi.authorizationCodeUri()
        .scope("user-read-playback-state,user-read-recently-played, user-read-currently-playing, playlist-read-private, playlist-modify-public, playlist-modify-private, user-read-email")
        .show_dialog(true)
        .build();

        final URI uri = authCodeUriReq.execute();
        return uri.toString();
    }
}
