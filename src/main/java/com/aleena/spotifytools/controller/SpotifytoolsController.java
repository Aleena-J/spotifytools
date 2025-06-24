package com.aleena.spotifytools.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;



import java.io.IOException;

import com.aleena.spotifytools.config.SpotifyConfig;
import com.aleena.spotifytools.service.GetRecentlyPlayedService;
import com.aleena.spotifytools.service.GetUserRecentSongsService;
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
    public void spotifyLogin(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        if(userId.equals("noID")){
            SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
            response.sendRedirect(loginService.spotifyLogin(spotifyApi));
        }else{
            response.sendRedirect("get-recently-played");
        }
    }

    @GetMapping("/callback")
    public void getUserDetails(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        Cookie cookie = new Cookie("userId", redirectService.getDetails(userCode, spotifyApi));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        response.addCookie(cookie);
        response.sendRedirect("get-recently-played");
    }
    
    //TODO: Redirect for when noID

    @GetMapping("get-recently-played")
    public String getRecentlyPlayed(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return recentlyPlayedService.recentlyPlayed(spotifyApi, userId);
    }


    @GetMapping("sort-playlist-popularity")
    public String sortStatus(@CookieValue(value = "userId", defaultValue = "noID") String userId, HttpServletResponse response) throws IOException{
        SpotifyApi spotifyApi = spotifyConfig.spotifyApi();
        return sortByPopularityService.sortByPop(spotifyApi, userId);
    }
  
}
