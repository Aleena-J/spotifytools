package com.aleena.spotifytools.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import java.io.IOException;

import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.service.GetRecentlyPlayedService;
import com.aleena.spotifytools.service.LoginService;
import com.aleena.spotifytools.service.RedirectService;
import com.aleena.spotifytools.service.SortByPopularityService;

import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/login")
    @ResponseBody
    public void spotifyLogin(HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        response.sendRedirect(loginService.spotifyLogin(spotifyApi));
    }

    @GetMapping("/callback")
    public void getUserDetails(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        Cookie cookie = new Cookie("userId", redirectService.getDetails(userCode, spotifyApi));
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        response.sendRedirect("get-recently-played");
    }
    

    @GetMapping("get-recently-played")
    public String getRecentlyPlayed(@CookieValue(value = "userId") String userId){
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return recentlyPlayedService.recentlyPlayed(spotifyApi, userId);
    }


    @GetMapping("sort-playlist-popularity")
    public String sortStatus(){
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return sortByPopularityService.sortByPop(spotifyApi);
    }
  
}
