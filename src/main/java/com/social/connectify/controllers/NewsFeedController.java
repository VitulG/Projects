package com.social.connectify.controllers;

import com.social.connectify.dto.NewsFeedDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PostNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.services.NewsFeedService.NewsFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/newsFeed")
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

    @Autowired
    public NewsFeedController(NewsFeedService newsFeedService) {
        this.newsFeedService = newsFeedService;
    }

    @GetMapping("/feed")
    public ResponseEntity<List<NewsFeedDto>> getNewsFeed(@RequestHeader("Authorization") String token) {
        try {
            List<NewsFeedDto> response = newsFeedService.getNewsFeed(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostNotFoundException | UserNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch(InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception exception) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
