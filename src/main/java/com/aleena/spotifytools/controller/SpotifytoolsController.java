package com.aleena.spotifytools.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.service.GetRecentlyPlayedService;
import com.aleena.spotifytools.service.GetRepeatsService;
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
    private GetRecentlyPlayedService recentlyPlayedService;
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

    @GetMapping("/login")
    @ResponseBody
    public void spotifyLogin(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        if(userId.equals("noID")){
            SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
            response.sendRedirect(loginService.spotifyLogin(spotifyApi));
        }else{
            response.sendRedirect("get-recently-played");
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public void logout(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        
    }

    @GetMapping("/callback")
    public void getUserDetails(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        Cookie cookie = new Cookie("userId", redirectService.getDetails(userCode, spotifyApi));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        response.addCookie(cookie);
        response.sendRedirect("repeats");
    }
    
    //TODO: Redirect for when noID

    @GetMapping("get-recently-played")
    public String getRecentlyPlayed(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return recentlyPlayedService.recentlyPlayed(spotifyApi, userId);
    }


    @PostMapping("sort-playlist-popularity")
    public String sortStatus(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return sortByPopularityService.sortByPop(spotifyApi, userId);
    }

    @GetMapping("repeats")
    public String getUserRepeats(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        return repeatsService.repeats(userId);
    }

    @GetMapping("skips")
    public String getUserSkips(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        return new String();
    }
    
    @DeleteMapping("delete-account")
    public String deleteUser(@CookieValue(value = "userId", defaultValue = "noID") String userId){
        return new String();
    }
    

    @PostMapping("track-skip")
    public String trackUserSkip(@CookieValue(value = "userId", defaultValue = "noID") String userId) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return skippedSongsTrackService.trackSkip(spotifyApi, userId);
    }
    
  
}
