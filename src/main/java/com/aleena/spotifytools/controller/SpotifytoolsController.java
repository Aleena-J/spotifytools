package com.aleena.spotifytools.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.dto.PlaylistCreateDTO;
import com.aleena.spotifytools.dto.PlaylistDTO;
import com.aleena.spotifytools.dto.SongDTO;
import com.aleena.spotifytools.dto.SortRequestDTO;
import com.aleena.spotifytools.service.AddToQueueService;
import com.aleena.spotifytools.service.CreateRepeatPlaylistService;
import com.aleena.spotifytools.service.DeleteUserService;
import com.aleena.spotifytools.service.GetRepeatsService;
import com.aleena.spotifytools.service.GetUsersPlaylistsService;
import com.aleena.spotifytools.service.LoginService;
import com.aleena.spotifytools.service.RedirectService;
import com.aleena.spotifytools.service.SkippedSongsTrackService;
import com.aleena.spotifytools.service.SortByPopularityService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import se.michaelthelin.spotify.SpotifyApi;


@RestController
public class SpotifytoolsController {
    @Autowired
    private SpotifyConfig spotifyConfig;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RedirectService redirectService;
    @Autowired
    private SortByPopularityService sortByPopularityService;
    @Autowired
    private SkippedSongsTrackService skippedSongsTrackService;
    @Autowired
    private GetRepeatsService repeatsService;
    @Autowired
    private GetUsersPlaylistsService playlistsService;
    @Autowired
    private DeleteUserService deleteUserService;
    @Autowired
    private CreateRepeatPlaylistService repeatPlaylistService;
    @Autowired
    private AddToQueueService queueService;


    //TODO: what to do in case of "noID"
    @GetMapping("/login")
    public String spotifyLogin() throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        String authUrl = loginService.spotifyLogin(spotifyApi);
        return authUrl;
    }

    @PostMapping("/callback")
    public void getUserDetails(@RequestBody String code, HttpServletResponse response) throws IOException{
        String userCode = code;
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        String userId = redirectService.getDetails(userCode, spotifyApi);
        Cookie cookie = new Cookie("userId", userId);
        cookie.setHttpOnly(false);
        //TODO: SET TRUE
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Set-Cookie", "userId=" + userId + "; Max-Age=86400; Path=/; Secure; SameSite=None");
    }

    @GetMapping("users-playlists")
    public List<PlaylistDTO> getUsersPlaylists(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return playlistsService.getUsersPlaylists(spotifyApi, userId);
    }
    

    @PostMapping("sort-playlist-popularity")
    public String sortStatus(@CookieValue(value = "userId", defaultValue = "noID") String userId, @RequestBody SortRequestDTO request, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return sortByPopularityService.sortByPop(spotifyApi, userId, request.getPlaylistLink(), request.getMethod(), request.getOrder());
    }

    @GetMapping("repeats")
    public List<SongDTO> getUserRepeats(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        return repeatsService.repeats(userId);
    }

    
    @DeleteMapping("delete-account")
    public String deleteUser(@CookieValue(value = "userId", defaultValue = "noID") String userId) throws IOException{
        return deleteUserService.deleteUser(userId);
    }
    
    
    @PostMapping("create-repeat-playlist")
    public String postMethodName(@CookieValue(value = "userId", defaultValue = "noID") String userId, @RequestBody PlaylistCreateDTO request) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        String period = request.getPeriod();
        List<SongDTO> songs = request.getSongs();
        return repeatPlaylistService.createRepeatPlaylist(spotifyApi, userId, period, songs);
    }

    @PostMapping("add-to-queue")
    public String postMethodName(@CookieValue(value = "userId", defaultValue = "noID") String userId, @RequestBody String uri) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return queueService.addToQueue(spotifyApi, userId, uri);
    }
    
     @GetMapping("skips")
    public String getUserSkips(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        return new String();
    }

    @PostMapping("track-skip")
    public String trackUserSkip(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return skippedSongsTrackService.trackSkip(spotifyApi, userId);
    }
  
}
