package com.social.connectify.services.PostService;

import com.social.connectify.dto.*;
import com.social.connectify.exceptions.*;

import java.util.List;

public interface PostService {
    String createPost(String token, CreatePostRequestDto createPostRequestDto) throws InvalidTokenException,
            PostCreationException;
    List<PostResponseDto> userPosts(String token) throws InvalidTokenException, UserPostNotFoundException;
    String likePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException;
    String commentPost(String token, Long postId, UserCommentRequestDto userCommentRequestDto) throws InvalidTokenException,
            PostNotFoundException;
    String sharePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException,
            PostNotFoundException;
    String editPost(String token, Long postId, EditPostRequestDto editPostRequestDto) throws InvalidTokenException, UserNotFoundException,
            PostNotFoundException, UnauthorizedUserException;
    String deletePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException, UnauthorizedUserException;
    List<LikedPostUserDto> getUsersWhoLikedPost(String token, Long postId) throws InvalidTokenException, UserNotFoundException,
            PostNotFoundException;
    List<PostCommentUsersResponseDto> getUsersWhoCommentOnPost(String token, Long postId) throws InvalidTokenException, PostNotFoundException;
}
