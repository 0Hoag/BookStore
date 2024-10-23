package com.example.post_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.post_service.dto.FileUtils;
import com.example.post_service.dto.PageResponse;
import com.example.post_service.dto.request.*;
import com.example.post_service.dto.response.*;
import com.example.post_service.entity.MediaMetadata;
import com.example.post_service.entity.Post;
import com.example.post_service.exception.AppException;
import com.example.post_service.exception.ErrorCode;
import com.example.post_service.mapper.PostMapper;
import com.example.post_service.repository.FileRepository;
import com.example.post_service.repository.ImageRepository;
import com.example.post_service.repository.PostRepository;
import com.example.post_service.repository.httpClient.CommentClient;
import com.example.post_service.repository.httpClient.IdentityClient;
import com.example.post_service.repository.httpClient.InteractionClient;
import com.example.post_service.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostMapper postMapper;
    PostRepository postRepository;
    IdentityClient identityClient;
    ProfileClient profileClient;
    CommentClient commentClient;
    InteractionClient interactionClient;
    DateTimeFormatter dateTimeFormatter;
    FileRepository fileRepository;
    ImageService imageService;
    ImageRepository imageRepository;
    VideoService videoService;


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
        postResponse.setUserId(user.getUserId());

        return postResponse;
    }

    public PostResponse uploadMediaToPost(String postId, MultipartFile file) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        if (!FileUtils.validateFile(file)) {
            log.error("Invalid file: {}", file.getOriginalFilename());
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }
        log.info("OriginalFilename: {}", file.getOriginalFilename()); // type file ex: image or video

        try {
            boolean isVideo = FileUtils.isVideoFile(file.getOriginalFilename());
            Map<String, Object> uploadOptions = new HashMap<>();

            if (isVideo) {
                uploadOptions.put("resource_type", "video");
            }

            // Upload file gốc lên Cloudinary
            Map<String, Object> uploadResult = cloudinaryConfig()
                    .uploader()
                    .upload(file.getBytes(), uploadOptions);

            // Lấy thông tin từ kết quả upload
            String originalUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            // Tạo metadata cho media
            MediaMetadata metadata = new MediaMetadata();
            metadata.setType(isVideo ? "video" : "image");
            metadata.setPublicId(publicId);
            metadata.setOriginalUrl(originalUrl);

            // Tạo các version khác nhau bằng URL transformation
            Map<String, String> versions = new HashMap<>();

            if (isVideo) {
                // Tạo URL cho video version 720p
                String video720pUrl = cloudinaryConfig().url()
                        .transformation(new Transformation().width(720).crop("scale"))
                        .format("mp4")
                        .generate(publicId);
                versions.put("w720", video720pUrl);
            } else {
                // Tạo URL cho ảnh version 1080p
                String image1080pUrl = cloudinaryConfig().url()
                        .transformation(new Transformation().width(1080).crop("scale"))
                        .generate(publicId);
                versions.put("w1080", image1080pUrl);
            }

            // Tạo thumbnail URL
            String thumbnailUrl = cloudinaryConfig().url()
                    .transformation(new Transformation().width(1000).height(1000).crop("thumb"))
                    .generate(publicId);

            metadata.setThumbnailUrl(thumbnailUrl);
            metadata.setVersions(versions);

            // Cập nhật post với URL và metadata mới
            if (post.getMedias() == null) {
                post.setMedias(new HashSet<>());
            }
            if (post.getMediaMetadata() == null) {
                post.setMediaMetadata(new HashMap<>());
            }

            post.getMedias().add(originalUrl);
            post.getMediaMetadata().put(publicId, metadata);
            post.setUpdatedAt(LocalDateTime.now());

            Post savedPost = postRepository.save(post);

            return postMapper.toPostResponse(savedPost);

        } catch (Exception e) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
//        Cloudinary cloudinary = null;
//        Map config = new HashMap();
//        config.put("cloud_name", "ddclol9ih");
//        config.put("api_key", "484769914577293");
//        config.put("api_secret", "41IJGj5NP901wP-74TR-fmIj0OQ");
//        cloudinary = new Cloudinary(config);
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "ddclol9ih",
                "api_key", "484769914577293",
                "api_secret", "41IJGj5NP901wP-74TR-fmIj0OQ",
                "secure", true
        ));
    }

    public void removeImagePost(String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        Set<String> postImages = new HashSet<>(post.getMedias());

        for (String mediaUrl : postImages) {
            try {
                String publicId = extractPublicIdFromUrl(mediaUrl);
                log.info("publicId: {}", publicId);

                Map<String, Object> params = new HashMap<>();
                if (FileUtils.isVideoFile(mediaUrl)) {
                    params.put("resource_type", "video");
                    log.info("Delete video with publicId: {}", publicId);
                }else {
                    log.info("Delete image with publicId: {}", publicId);
                }

                Map result = cloudinaryConfig().uploader().destroy(publicId, params);

                if ("ok".equals(result.get("result"))) {
                    log.info("Successfully delete media from Cloudinary");
                    post.getMedias().remove(mediaUrl);
                }else {
                    log.error("Failed to delete media from Cloudinary");
                    throw new AppException(ErrorCode.REMOVE_FILE_FAIL);
                }

            } catch (IOException e) {
                throw new AppException(ErrorCode.REMOVE_FILE_FAIL);
            }
        }
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
                            postResponse.setUserId(userResponse.getUserId());

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
                            postResponse.setUserId(userResponse.getUserId());

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

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setUserId(user.getUserId());
//        postResponse.setLikes(likeResponses);

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
                    postResponse.setUserId(user.getUserId());
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

        if (post.getMedias() == null) {
            selectedLikeToRemove(post.getLikes()); //delete all like to post
            selectedCommentToRemove(post.getComments()); // delete all comment to post
        }else {
            removeImagePost(postId);
            selectedLikeToRemove(post.getLikes()); //delete all like to post
            selectedCommentToRemove(post.getComments()); // delete all comment to post
        }
        postRepository.deleteById(postId);
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        int uploadIndex = -1;
        for (int i = 0; i < urlParts.length; i++) {
            if ("upload".equals(urlParts[i])) {
                uploadIndex = i;
                break;
            }
        }

        if (uploadIndex == -1 || uploadIndex == urlParts.length - 1) {
            throw new IllegalArgumentException("Invalid Cloudinary URL format");
        }

        return String.join("/", Arrays.stream(urlParts, uploadIndex + 1, urlParts.length)
                        .filter(part -> !part.startsWith("v"))
                        .collect(Collectors.toList()))
                .replaceFirst("[.][^.]+$", "");
    }

    public void deleteAllPost() {
        postRepository.deleteAll();
    }

    public PostResponse updatePost(
            String postId,
            String content,
            Set<String> medias) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        post.setContent(content);
        post.setMedias(medias);
        post.setUpdatedAt(LocalDateTime.now());
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