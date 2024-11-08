package com.example.Interaction_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Interaction_service.dto.request.AuthenticationRequest;
import com.example.Interaction_service.dto.request.CreateLikeRequest;
import com.example.Interaction_service.dto.response.*;
import com.example.Interaction_service.entity.Like;
import com.example.Interaction_service.exception.AppException;
import com.example.Interaction_service.exception.ErrorCode;
import com.example.Interaction_service.mapper.LikeMapper;
import com.example.Interaction_service.repository.LikeRepository;
import com.example.Interaction_service.repository.httpClient.IdentityClient;
import com.example.Interaction_service.repository.httpClient.PostClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LikeService {
    LikeRepository likeRepository;
    LikeMapper likeMapper;
    PostClient postClient;
    IdentityClient identityClient;

    public LikeResponse createLike(CreateLikeRequest request) {
        String token = generationToken();

        var post = getPost(request.getPostId(), token);

        UserResponse user = getUserInformationBasic(request.getUserId(), token);

        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        var like = likeMapper.toLike(request);
        like.setCreatedAt(LocalDateTime.now());
        like.setPost(post.getPostId());

        return likeMapper.toLikeResponse(likeRepository.save(like));
    }

    public LikeResponse getLike(String likeId) {
        var like = likeRepository.findById(likeId).orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_EXISTED));
        return likeMapper.toLikeResponse(like);
    }

    public List<LikeResponse> getAllLike() {
        return likeRepository.findAll().stream()
                .map(like -> {
                    var like1 = likeRepository
                            .findById(like.getLikeId())
                            .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_EXISTED));
                    return likeMapper.toLikeResponse(like1);
                })
                .collect(Collectors.toList());
    }

    public Set<Like> selectedLike(Set<String> strings) {
        Set<Like> likeSet = likeRepository.findAllById(strings).stream()
                .map(like -> {
                    Like like1 = likeRepository
                            .findById(like.getLikeId())
                            .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_EXISTED));
                    return like1;
                })
                .collect(Collectors.toSet());
        return likeSet;
    }

    public Set<LikeResponse> selectedLikeResponse(Set<Like> likes) {
        Set<LikeResponse> likeResponses = likes.stream()
                .map(like -> {
                    LikeResponse likeResponse = likeMapper.toLikeResponse(like);
                    return likeResponse;
                })
                .collect(Collectors.toSet());
        return likeResponses;
    }

    public void deleteLike(String likeId) {
        likeRepository.findById(likeId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        likeRepository.deleteById(likeId);
    }

    public void deleteAllLike() {
        likeRepository.deleteAll();
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
