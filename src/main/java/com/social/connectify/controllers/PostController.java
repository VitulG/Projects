package com.social.connectify.controllers;


import com.social.connectify.dto.*;
import com.social.connectify.exceptions.*;
import com.social.connectify.services.PostService.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<String> createPost( @RequestHeader("Authorization") String token,
                                              @RequestBody CreatePostRequestDto createPostRequestDto) {
        // Implement post creation logic here
        try {
            String response = postService.createPost(token, createPostRequestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PostCreationException postCreationException) {
            return new ResponseEntity<>(postCreationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-posts")
    public ResponseEntity<List<PostResponseDto>> getPosts(@RequestHeader("Authorization") String token) {
        try {
            List<PostResponseDto> posts = postService.userPosts(token);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (UserPostNotFoundException userPostNotFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<String> likeOnPost(@RequestHeader("Authorization") String token,
                                                   @PathVariable("postId") Long postId) {
        try {
            String post = postService.likePost(token, postId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(UserNotFoundException | PostNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/comments/{postId}")
    public ResponseEntity<String> commentOnPost( @RequestHeader("Authorization") String token,
                                                 @PathVariable("postId") Long postId,
                                                 @RequestBody UserCommentRequestDto userCommentRequestDto) {
        try {
            String response = postService.commentPost(token, postId, userCommentRequestDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PostNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/share/{postId}")
    public ResponseEntity<String> sharePost(@RequestHeader("Authorization") String token,
                                            @PathVariable("postId") Long postId) {
        try {
            String response = postService.sharePost(token, postId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (PostNotFoundException | UserNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/like-user/{postId}")
    public ResponseEntity<List<LikedPostUserDto>> getUserWhoLikedPost(@RequestHeader("Authorization") String token,
                                                                      @PathVariable("postId") Long postId) {
        try {
            List<LikedPostUserDto> likedUsers = postService.getUsersWhoLikedPost(token, postId);
            return new ResponseEntity<>(likedUsers, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException | PostNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/post-comments/{postId}")
    public ResponseEntity<List<PostCommentUsersResponseDto>> getPostComments(@RequestHeader("Authorization") String token,
                                                                          @PathVariable("postId") Long postId) {
        try {
            List<PostCommentUsersResponseDto> postComments = postService.getUsersWhoCommentOnPost(token, postId);
            return new ResponseEntity<>(postComments, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (PostNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update-post/{id}")
    public ResponseEntity<String> editPost(@RequestHeader("Authorization") String token, @PathVariable("id") Long postId,
                                           EditPostRequestDto editPostRequestDto) {
        try {
            String response = postService.editPost(token, postId, editPostRequestDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException | UnauthorizedUserException unauthorizedException) {
            return new ResponseEntity<>(unauthorizedException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(UserNotFoundException | PostNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String token,
                                             @PathVariable("postId") Long postId) {
        try {
            String response = postService.deletePost(token, postId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException | UnauthorizedUserException unauthorizedException) {
            return new ResponseEntity<>(unauthorizedException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(UserNotFoundException | PostNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
