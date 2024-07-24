package com.example.post_service.service;

import com.example.post_service.dto.request.AuthenticationRequest;
import com.example.post_service.dto.request.CreatePostRequest;
import com.example.post_service.dto.response.*;
import com.example.post_service.entity.PostStatusBook;
import com.example.post_service.exception.AppException;
import com.example.post_service.exception.ErrorCode;
import com.example.post_service.mapper.PostMapper;
import com.example.post_service.repository.PostRepository;
import com.example.post_service.repository.httpClient.BookClient;
import com.example.post_service.repository.httpClient.IdentityClient;
import com.example.post_service.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
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
    BookClient bookClient;
    ProfileClient profileClient;
    IdentityClient identityClient;

    public PostResponse createPost(CreatePostRequest request) {
        try {
            // Get token from identity-service
            String token = getTokenFromIdentityService();
            log.info("Token {}", token);

            // Fetch book information
            BookResponse bookResponse = fetchBookInformation(request.getBookId(), token);
            log.info("BookResponse {}", bookResponse);

            // Fetch user profile information
            UserProfileResponse userProfileResponse = fetchUserProfile(request.getProfileId(), token);
            log.info("UserProfileResponse {}", userProfileResponse);

            // Fetch user information
            UserResponse userResponse = fetchUserInformation(userProfileResponse.getUser().getId(), token);
            log.info("UserResponse {}", userResponse);

            // Set roles to userResponse
            Set<RoleResponse> roleResponses = userResponse.getRoles();
            userResponse.setRoles(new HashSet<>(roleResponses));
            userProfileResponse.setUser(userResponse);

            // Create and save post status book
            PostStatusBook postStatusBook = postMapper.toPostStatusBook(request);
            postStatusBook.setCreatedAt(LocalDate.now());
            postStatusBook = postRepository.save(postStatusBook);
            log.info("PostStatusBook {}", postStatusBook);

            // Create PostResponse
            PostResponse postResponse = postMapper.toPostResponse(postStatusBook);
            postResponse.setProfile(userProfileResponse);
            postResponse.setBook(bookResponse);
            postResponse.getProfile().getUser().setRoles(roleResponses);

            log.info("PostResponse {}", postResponse);

            return postResponse;
        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage());
            throw new RuntimeException("Error creating post", e);
        }
    }

    public List<PostResponse> getAllPost() {
        List<PostStatusBook> postStatusBooks = postRepository.findAll();

        String token = getTokenFromIdentityService();
        log.info("Token: {}", token);

        return postRepository.findAll().stream()
                .map(postStatusBook -> {
                    BookResponse bookResponse = fetchBookInformation(postStatusBook.getBookId(), token);

                    if (bookResponse == null) {
                        throw new AppException(ErrorCode.POST_NOT_EXISTED);
                    }

                    UserProfileResponse userProfileResponse = fetchUserProfile(postStatusBook.getProfileId(), token);

                    UserResponse userResponse = fetchUserInformation(userProfileResponse.getUser().getId(), token);

                    Set<RoleResponse> roleResponses = userResponse.getRoles();
                    userResponse.setRoles(roleResponses);

                    PostResponse postResponse = postMapper.toPostResponse(postStatusBook);
                    userProfileResponse.setUser(userResponse);
                    postResponse.setProfile(userProfileResponse);
                    postResponse.setBook(bookResponse);

                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    public PostResponse getPost(String postId) {
        String token = getTokenFromIdentityService();

        PostStatusBook postStatusBook = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        BookResponse bookResponse = fetchBookInformation(postStatusBook.getBookId(), token);
        if (bookResponse == null) {
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }

        UserProfileResponse userProfileResponse = fetchUserProfile(postStatusBook.getProfileId(), token);

        if (userProfileResponse == null) {
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }

        PostResponse postResponse = postMapper.toPostResponse(postStatusBook);
        postResponse.setProfile(userProfileResponse);
        postResponse.setBook(bookResponse);
        return postResponse;
    }

    private String getTokenFromIdentityService() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> authResponse = identityClient.getToken(authenticationRequest);
        return authResponse.getResult().getToken();
    }

    private BookResponse fetchBookInformation(String bookId, String token) {
        ApiResponse<BookResponse> apiResponse = bookClient.getBook(bookId, "Bearer " + token);
        return apiResponse.getResult();
    }

    private UserProfileResponse fetchUserProfile(String profileId, String token) {
        ApiResponse<UserProfileResponse> userProfileResponseApiResponse = profileClient.getProfile(profileId, "Bearer " + token);
        return userProfileResponseApiResponse.getResult();
    }

    private UserResponse fetchUserInformation(String userId, String token) {
        ApiResponse<UserResponse> userResponseApiResponse = identityClient.getUser(userId, "Bearer " + token);
        return userResponseApiResponse.getResult();
    }

    public void deletePost(String postId) {
        PostStatusBook postStatusBook = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        postRepository.deleteById(postId);
    }

    public void deleteAllPost() {
        postRepository.deleteAll();
    }
}
