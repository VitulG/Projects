package com.social.connectify.services.PostService;

import com.social.connectify.dto.*;
import com.social.connectify.enums.NotificationStatus;
import com.social.connectify.enums.NotificationType;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final TokenRepository tokenRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public PostServiceImpl(TokenRepository tokenRepository, ImageRepository imageRepository, UserRepository userRepository,
                           PostRepository postRepository, VideoRepository videoRepository, LikeRepository likeRepository,
                           NotificationRepository notificationRepository, CommentRepository commentRepository) {
        this.imageRepository = imageRepository;
        this.tokenRepository = tokenRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String createPost(String token, CreatePostRequestDto createPostRequestDto) throws InvalidTokenException, PostCreationException {
        User user = validateToken(token);

        if(createPostRequestDto.getImageUrl() == null && createPostRequestDto.getImageUrl().isEmpty()
                && createPostRequestDto.getContent() == null || createPostRequestDto.getContent().isEmpty()) {
            throw new PostCreationException("Unable to post because content is null");
        }

        Post post = new Post();
        post.setUser(user);
        post.setContent(createPostRequestDto.getContent());
        post.setCreatedAt(LocalDateTime.now());

        if(post.getImages() == null) {
            post.setImages(new ArrayList<>());
        }

        if(post.getVideos() == null) {
            post.setVideos(new ArrayList<>());
        }

        if(createPostRequestDto.getImageUrl()!= null) {
            Image image = new Image();
            image.setImageUrl(createPostRequestDto.getImageUrl());
            image.setCreatedAt(LocalDateTime.now());
            image.setPost(post);
            imageRepository.save(image);
            post.getImages().add(image);
        }

        if(createPostRequestDto.getVideoUrl() != null) {
            Video video = new Video();
            video.setPost(post);
            video.setVideoLink(createPostRequestDto.getVideoUrl());
            video.setCreatedAt(LocalDateTime.now());
            videoRepository.save(video);
            post.getVideos().add(video);
        }

        if(user.getUserPosts() == null) {
            user.setUserPosts(new ArrayList<>());
        }
        user.getUserPosts().add(post);
        userRepository.save(user);

        for(User friend : user.getUserFriends()) {
            Notification notification = new Notification();
            notification.setStatus(NotificationStatus.UNREAD);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMessage(user.getFirstName()+" "+user.getLastName()+" added a new post");
            notification.setUser(friend);
            notificationRepository.save(notification);
        }
        postRepository.save(post);
        return "posted successfully";
    }

    @Override
    @Transactional
    public List<PostResponseDto> userPosts(String token) throws InvalidTokenException, UserPostNotFoundException {
        User user = validateToken(token);

        if(user.getUserPosts() == null || user.getUserPosts().isEmpty()) {
            throw new UserPostNotFoundException("Unable to get any user post");
        }

        List<PostResponseDto> response = new ArrayList<PostResponseDto>();
        for(Post post : user.getUserPosts()) {
            response.add(post.convertToPostResponseDto());
        }
        return response;
    }

    @Override
    @Transactional
    public String likePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException {
        User user = validateToken(token);

        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not available");
        }
        Post post = optionalPost.get();

        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        if(existingLike.isPresent()) {
            return "Post already liked by user";
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);

        if(post.getLikes() == null) {
            post.setLikes(new ArrayList<>());
        }

        post.getLikes().add(like);

        if(post.getNumberOfLikes() == null) {
            post.setNumberOfLikes(0L);
        }
        post.setNumberOfLikes(post.getNumberOfLikes() + 1);
        postRepository.save(post);
        createNotification(user, postId, NotificationType.LIKE);
        return "post liked";
    }

    @Override
    @Transactional
    public String commentPost(String token, Long postId, UserCommentRequestDto userCommentRequestDto) throws InvalidTokenException, PostNotFoundException {
        if(userCommentRequestDto.getComment() == null || userCommentRequestDto.getComment().isEmpty()) {
            return "comment can not be empty";
        }

        User user = validateToken(token);

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not available");
        }

        Post post = optionalPost.get();
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(userCommentRequestDto.getComment());
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        post.getComments().add(comment);

        if(post.getNumberOfComments() == null) {
            post.setNumberOfComments(0L);
        }
        post.setNumberOfComments(post.getNumberOfComments()+1);
        postRepository.save(post);

        createNotification(user, postId, NotificationType.COMMENT);

        return "commented on the post";
    }

    @Override
    @Transactional
    public String sharePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException {
        User user = validateToken(token);

        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not available");
        }
        Post post = optionalPost.get();
        if(post.getNumberOfTimesPostShared() == null) {
            post.setNumberOfTimesPostShared(0L);
        }
        post.setNumberOfTimesPostShared(post.getNumberOfTimesPostShared() + 1);
        postRepository.save(post);
        createNotification(user, postId, NotificationType.SHARE);
        return "post shared";
    }

    @Override
    @Transactional
    public String editPost(String token, Long postId, EditPostRequestDto editPostRequestDto) throws InvalidTokenException, UserNotFoundException, PostNotFoundException, UnauthorizedUserException {
        User user = validateToken(token);

        if (user == null) {
            throw new UserNotFoundException("User does not exist");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("No post found with the given ID");
        }

        Post post = optionalPost.get();

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedUserException("You are not authorized to edit this post");
        }

        if(post.isDeleted()) {
            throw new PostNotFoundException("This post has been deleted. can not be modified");
        }

        post.setContent(editPostRequestDto.getNewContent());
        post.setUpdatedAt(LocalDateTime.now());

        // Clear existing images
        List<Image> images = post.getImages();
        if (images != null) {
            imageRepository.deleteAll(images);
            post.getImages().clear();
        }

        // Add new image
        if (editPostRequestDto.getImageUrl() != null && !editPostRequestDto.getImageUrl().isEmpty()) {
            Image image = new Image();
            image.setPost(post);
            image.setCreatedAt(LocalDateTime.now());
            image.setUpdatedAt(LocalDateTime.now());
            image.setImageUrl(editPostRequestDto.getImageUrl());
            imageRepository.save(image);
            post.getImages().add(image);
        }

        // Clear existing videos
        List<Video> videos = post.getVideos();
        if (videos != null) {
            videoRepository.deleteAll(videos);
            post.getVideos().clear();
        }

        // Add new video
        if (editPostRequestDto.getVideoUrl() != null && !editPostRequestDto.getVideoUrl().isEmpty()) {
            Video video = new Video();
            video.setCreatedAt(LocalDateTime.now());
            video.setUpdatedAt(LocalDateTime.now());
            video.setVideoLink(editPostRequestDto.getVideoUrl());
            video.setPost(post);
            videoRepository.save(video);
            post.getVideos().add(video);
        }
        postRepository.save(post);
        return "Post updated successfully";
    }

    @Override
    @Transactional
    public String deletePost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException, UnauthorizedUserException {
        User user = validateToken(token);

        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }

        Post post = optionalPost.get();

        if(!post.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedUserException("You are not authorized to delete this post");
        }

        if(post.getImages() != null) {
            imageRepository.deleteAll(post.getImages());
            post.setImages(new ArrayList<>());
        }

        if(post.getLikes() != null) {
            likeRepository.deleteAll(post.getLikes());
            post.setLikes(new ArrayList<>());
        }

        if(post.getComments()!= null) {
            commentRepository.deleteAll(post.getComments());
            post.setComments(new ArrayList<>());
        }

        if(post.getVideos()!= null) {
            videoRepository.deleteAll(post.getVideos());
            post.setVideos(new ArrayList<>());
        }

        post.setNumberOfLikes(0L);
        post.setNumberOfComments(0L);
        post.setNumberOfTimesPostShared(0L);

        post.setContent("");
        post.setDeleted(true);

        post.setUser(null);

        postRepository.save(post);

        return "Post deleted successfully";
    }

    @Override
    public List<LikedPostUserDto> getUsersWhoLikedPost(String token, Long postId) throws InvalidTokenException, UserNotFoundException, PostNotFoundException {
        User user = validateToken(token);

        if (user == null) {
            throw new InvalidTokenException("Invalid token");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not available");
        }

        Post post = optionalPost.get();

        Optional<List<Like>> optionalLikes = likeRepository.findAllByPost(post);

        if(optionalLikes.isEmpty()) {
            return new ArrayList<>();  // No likes found for this post
        }

        List<Like> likes = optionalLikes.get();

        // Initialize the result list
        List<LikedPostUserDto> usersWhoLiked = new ArrayList<>();

        for (Like like : likes) {
            LikedPostUserDto userWhoLiked = new LikedPostUserDto();
            userWhoLiked.setFistName(like.getUser().getFirstName());
            userWhoLiked.setLastName(like.getUser().getLastName());
            usersWhoLiked.add(userWhoLiked);
        }
        return usersWhoLiked;
    }

    @Override
    public List<PostCommentUsersResponseDto> getUsersWhoCommentOnPost(String token, Long postId) throws InvalidTokenException, PostNotFoundException {
        User user = validateToken(token);

        if(user == null) {
            throw new InvalidTokenException("User not found");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);

        if(optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }

        Post post = optionalPost.get();

        Optional<List<Comment>> optionalComments = commentRepository.findAllByPost(post);

        if(optionalComments.isEmpty()) {
            return new ArrayList<>();  // No comments found for this post
        }

        List<Comment> comments = optionalComments.get();
        List<PostCommentUsersResponseDto> commentsRequest = new ArrayList<>();

        for(Comment comment : comments) {
            PostCommentUsersResponseDto commentedUser = new PostCommentUsersResponseDto();
            commentedUser.setFirstName(comment.getUser().getFirstName());
            commentedUser.setLastName(comment.getUser().getLastName());
            commentedUser.setComment(comment.getContent());
            commentsRequest.add(commentedUser);
        }
        return commentsRequest;
    }

    private User validateToken(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("No token found");
        }

        Token existingToken = optionalToken.get();

        if(existingToken.isDeleted() ||!existingToken.isActive() ||
                existingToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token is either expired, deleted, or inactive");
        }
        return existingToken.getUser();
    }

    private void createNotification(User user, Long postId, NotificationType type) throws PostNotFoundException {
        Optional<Post> postOwnerOptional = postRepository.findById(postId);

        if(postOwnerOptional.isEmpty()) {
            throw new PostNotFoundException("no post available");
        }

        User postOwner = postOwnerOptional.get().getUser();

        if(!postOwner.getEmail().equals(user.getEmail())) {
            Notification notification = new Notification();

            notification.setCreatedAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.UNREAD);
            notification.setUser(postOwner);
            if(type == NotificationType.LIKE) {
                notification.setMessage(user.getFirstName()+" "+user.getLastName()+" liked your post");
            }else if(type == NotificationType.COMMENT) {
                notification.setMessage(user.getFirstName()+" "+user.getLastName()+" commented on your post");
            }else if(type == NotificationType.SHARE) {
                notification.setMessage(user.getFirstName()+" "+user.getLastName()+" shared your post");
            }
            notificationRepository.save(notification);
        }
    }
}
