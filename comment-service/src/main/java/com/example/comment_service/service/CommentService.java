package com.example.comment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.comment_service.dto.request.AuthenticationRequest;
import com.example.comment_service.dto.request.CreateCommentRequest;
import com.example.comment_service.dto.request.UpdateCommentRequest;
import com.example.comment_service.dto.response.*;
import com.example.comment_service.exception.AppException;
import com.example.comment_service.exception.ErrorCode;
import com.example.comment_service.mapper.CommentMapper;
import com.example.comment_service.repository.CommentRepository;
import com.example.comment_service.repository.httpClient.IdentityClient;
import com.example.comment_service.repository.httpClient.PostClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentService {
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    PostClient postClient;
    IdentityClient identityClient;

    public CommentResponse createComment(CreateCommentRequest request) {
        String token = generationToken();

        var post = getPost(request.getPostId(), token);

        UserResponse user = getUserInformationBasic(request.getUserId(), token);

        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        var comment = commentMapper.toComment(request);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post.getPostId());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public CommentResponse getComment(String commentId) {
        var comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        return commentMapper.toCommentResponse(comment);
    }

    public List<CommentResponse> getAllComment() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    public void deleteComment(String commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        commentRepository.deleteById(commentId);
    }

    public void deleteAllComment() {
        commentRepository.deleteAll();
    }

    public CommentResponse updateCommentResponse(String commentId, UpdateCommentRequest request) {
        var user = getUserInformationBasic(request.getUserId(), generationToken());
        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
        var comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.updateComment(comment, request);
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public PostResponse getPost(String postId, String token) {
        ApiResponse<PostResponse> postResponseApiResponse = postClient.getPost(postId, "Bearer " + token);
        return postResponseApiResponse.getResult();
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
