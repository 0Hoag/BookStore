package com.example.post_service.service;

import com.example.post_service.dto.PageResponse;
import com.example.post_service.dto.request.*;
import com.example.post_service.dto.response.*;
import com.example.post_service.entity.Post;
import com.example.post_service.exception.AppException;
import com.example.post_service.exception.ErrorCode;
import com.example.post_service.mapper.PostMapper;
import com.example.post_service.repository.PostRepository;
import com.example.post_service.repository.httpClient.CommentClient;
import com.example.post_service.repository.httpClient.IdentityClient;
import com.example.post_service.repository.httpClient.InteractionClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostMapper postMapper;
    PostRepository postRepository;
    IdentityClient identityClient;
    CommentClient commentClient;
    InteractionClient interactionClient;
    B2StorageService b2StorageService;
    DateTimeFormatter dateTimeFormatter;


//    public PostResponse createPost(
////            String userId,
////            String content,
////            List<String> likes,
////            List<String> comments
////            List<MultipartFile> imageUrls,
////            List<MultipartFile> videoUrls
//            CreatePostRequest request) {
//        String token = generationToken();
//        UserResponse user = getUserInformationBasic(request.getUserId(), token);
//
//        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
//
//        var post = new Post();
//        post.setUserId(request.getUserId());
//        post.setContent(request.getContent());
//        postRepository.save(post);
//
////        Set<LikeResponse> response = likes.stream()
////                        .map(s -> {
////                            LikeResponse response1 = getLike(s, token);
////                            return response1;
////                        }).collect(Collectors.toSet());
////
////        Set<CommentResponse> commentResponses = comments.stream()
////                        .map(s -> {
////                            CommentResponse commentResponse = getComment(s, token);
////                            return commentResponse;
////                        }).collect(Collectors.toSet());
//
//        post.setLikes(likeResponses);
//        post.setComments(commentResponses);
//        post.setCreatedAt(LocalDateTime.now());
//        post.setUpdatedAt(LocalDateTime.now());
//
//
////        Set<String> img = new HashSet<>();
////        for (MultipartFile image : imageUrls) {
////            String fileId = b2StorageService.uploadFiles(image);
////            String downloadUrl = b2StorageService.getNativeDownloadUrl(fileId);
////            img.add(downloadUrl);
////        }
////
////        Set<String> vid = new HashSet<>();
////        for (MultipartFile video : videoUrls) {
////            String fileId = b2StorageService.uploadFiles(video);
////            String downloadUrl = b2StorageService.getNativeDownloadUrl(fileId);
////            vid.add(downloadUrl);
////        }
////
////        post.setImageUrls(img);
////        post.setVideoUrls(vid);
//
//        PostResponse postResponse = postMapper.toPostResponse(post);
//        postResponse.setUserId(user);
////        postResponse.setImageUrls(img);
////        postResponse.setVideoUrls(vid);
//
//        return postResponse;
//    }

    public PostResponse createPost(CreatePostRequest request) {
        String token = generationToken();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserResponse user = getUserInformationBasic(authentication.getName(), token);

        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        var post = postMapper.toPostStatusBook(request);

        post.setUserId(user.getUserId());

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setUserId(user);

        return postResponse;
    }

    public PageResponse<PostResponse> getPostWithUserId(String userId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending(); // maybe error
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = postRepository.findByUserId(userId, pageable);

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(post -> {
                            UserResponse userResponse = getUserInformationBasic(post.getUserId(), generationToken());
                            if (userResponse == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
                            PostResponse postResponse = postMapper.toPostResponse(post);
                            postResponse.setUserId(userResponse);

                            return postResponse;
                        }).collect(Collectors.toList()))
                .build();
    }

    public PageResponse<PostResponse> getMyPost(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Sort sort = Sort.by("createdAt").descending(); // maybe error
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = postRepository.findByUserId(userId, pageable);

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(post -> {
                            UserResponse userResponse = getUserInformationBasic(post.getUserId(), generationToken());
                            if (userResponse == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
                            PostResponse postResponse = postMapper.toPostResponse(post);
                            postResponse.setUserId(userResponse);

                            return postResponse;
                        }).collect(Collectors.toList()))
                .build();
    }

    public PostResponse getPost(String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String token = generationToken();
        UserResponse user = getUserInformationBasic(post.getUserId(), token);
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        Set<LikeResponse> likeResponses = post.getLikes();

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setUserId(user);
        postResponse.setLikes(likeResponses);

        return postResponse;
    }

    public PageResponse<PostResponse> getAll(int page, int size) {
        String token = generationToken();

        Sort sort = Sort.by("createdAt").descending(); // maybe error
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = postRepository.findAll(pageable);

        var postList = pageData.getContent().stream()
                .map(post -> {
                    UserResponse user = getUserInformationBasic(post.getUserId(), token);
                    if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
                    PostResponse postResponse = postMapper.toPostResponse(post);
                    postResponse.setCreated(dateTimeFormatter.format(postResponse.getCreatedAt()));
                    postResponse.setUserId(user);
                    return postResponse;
                }).collect(Collectors.toList());

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public PostResponse addCommentToPost(String postId, AddCommentToPostRequest request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Set<CommentResponse> commentResponses = request.getCommentId()
                        .stream().map(s -> {
                            CommentResponse commentResponse = getComment(s, generationToken());
                            return commentResponse;
                }).collect(Collectors.toSet());

        post.getComments().addAll(commentResponses);
        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setComments(commentResponses);
        return postResponse;
    }

    public PostResponse removeCommentToPost(String postId, RemoveCommentToPostRequest request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Set<CommentResponse> commentResponses = request.getCommentId()
                .stream().map(s -> {
                    CommentResponse commentResponse = getComment(s, generationToken());
                    deleteComments(s, generationToken());
                    return commentResponse;
                }).collect(Collectors.toSet());

        post.getComments().removeAll(commentResponses);
        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setComments(commentResponses);

        return postResponse;
    }

    public PostResponse updateCommentToPost(String postId, String commentId, UpdateCommentRequest request) {
        String token = generationToken();
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        Set<CommentResponse> commentResponses = post.getComments()
                .stream().map(commentResponse -> {
                    if (commentResponse.getCommentId().equals(commentId)) {
                        // update comment pass FeignClient
                        updateComment(commentId, request);
                        // take comment update
                        return getComment(commentId, token);
                    }
                    return commentResponse;
                }).collect(Collectors.toSet());

        post.setComments(commentResponses);
        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setComments(commentResponses);

        return postResponse;
    }

    public PostResponse addLikeToPost(String postId, AddLikeToPostRequest request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String token = generationToken();

        Set<LikeResponse> likeResponses = request.getLikeId()
                .stream().map(s -> {
                    LikeResponse likeResponse = getLike(s, token);
                    return likeResponse;
                }).collect(Collectors.toSet());

        post.getLikes().addAll(likeResponses);
        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setLikes(likeResponses);
        return postResponse;
    }

    public PostResponse removeLikeToPost(String postId, RemoveLikeToPostRequest request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Set<LikeResponse> likeResponses = request.getLikeId()
                        .stream().map(s -> {
                            LikeResponse response = getLike(s, generationToken());
                            deleteLike(response.getLikeId(), generationToken());
                            return response;
                }).collect(Collectors.toSet());

        post.getLikes().removeAll(likeResponses);
        postRepository.save(post);

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setLikes(likeResponses);

        return postResponse;
    }

    public void deletePost(String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        selectedLikeToRemove(post.getLikes()); //delete all like to post
        selectedCommentToRemove(post.getComments()); // delete all comment to post

        postRepository.deleteById(postId);
    }

    public void deleteAllPost() {
        postRepository.deleteAll();
    }

    public PostResponse updatePost(String postId, UpdatePostRequest request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        postMapper.updatePost(post, request);
        return postMapper.toPostResponse(postRepository.save(post));
    }

    public Set<CommentResponse> selectedCommentToRemove(Set<CommentResponse> comments) {
        Set<CommentResponse> commentSet = comments.stream()
                .map(commentResponse -> {
                    CommentResponse comment1 = getComment(commentResponse.getCommentId(), generationToken());
                    deleteComments(comment1.getCommentId(), generationToken());
                    return comment1;
                }).collect(Collectors.toSet());
        return commentSet;
    }

    public Set<LikeResponse> selectedLikeToRemove(Set<LikeResponse> likeResponses) {
        String token = generationToken();
        Set<LikeResponse> likeResponses1 = likeResponses.stream()
                .map(likeResponse -> {
                    LikeResponse response = getLike(likeResponse.getLikeId(), token);
                    deleteLike(response.getLikeId(), token);
                    return response;
                }).collect(Collectors.toSet());
        return likeResponses1;
    }

    public LikeResponse getLike(String likeId, String token) {
        ApiResponse<LikeResponse> response = interactionClient.getLike(likeId, "Bearer " + token);
        return response.getResult();
    }

    public void deleteLike(String likeId, String token) {
        interactionClient.deleteLike(likeId, "Bearer " + token);
    }

    public CommentResponse getComment(String commentId, String token) {
        ApiResponse<CommentResponse> commentResponseApiResponse = commentClient.getComment(commentId, "Bearer " + token);
        return commentResponseApiResponse.getResult();
    }

    public CommentResponse updateComment(String commentId, UpdateCommentRequest request) {
        String token = generationToken();
        ApiResponse<CommentResponse> commentResponseApiResponse = commentClient.updateCommentResponse(commentId, request, "Bearer " + token);
        return commentResponseApiResponse.getResult();
    }

    public void deleteComments(String commentId, String token) {
        commentClient.deleteComment(commentId, "Bearer " + token);
    }

    public String generationToken() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> getToken = identityClient.getToken(authenticationRequest);
        return getToken.getResult().getToken();
    }

    private UserResponse getUserInformationBasic(String userId, String token) {
        ApiResponse<UserResponse> userResponse = identityClient.getUserInformationBasic(userId, "Bearer " + token);
        return userResponse.getResult();
    }
}