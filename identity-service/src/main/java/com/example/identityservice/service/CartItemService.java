package com.example.identityservice.service;

import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.CartItemResponse;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.entity.CartItem;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.CartItemMapper;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.CartItemRepository;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.repository.httpclient.BookClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
    UserRepository userRepository;
    BookClient bookClient;
    UserMapper userMapper;
    CartItemRepository cartItemRepository;
    CartItemMapper cartItemMapper;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long valid_duration;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    public CartItemResponse createCart(CartItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = generateToken(user);
        //create CartItem
        CartItem cartItem = cartItemMapper.toCartItem(request);
        cartItem.setUser(user);
        cartItem.setBookId(request.getBookId());
        cartItem = cartItemRepository.save(cartItem);

        CartItemResponse cartItemResponse = cartItemMapper.toCartItemResponse(cartItem);
        BookResponse bookResponse = fetchBookResponse(cartItem.getBookId(), token);
        cartItemResponse.setBookId(bookResponse);

        return cartItemResponse;
    }
    //replace 7/9
    public CartItemResponse getCartItem(String cartItemId) {
        var cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_EXISTED));
        User user = userRepository.findById(cartItem.getUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = generateToken(user);
        CartItemResponse cartItemResponse = cartItemMapper.toCartItemResponse(cartItem);
        BookResponse bookResponse = fetchBookResponse(cartItem.getBookId(), token);
        cartItemResponse.setBookId(bookResponse);

        return cartItemResponse;
    }

    public List<CartItemResponse> getAllCartItem() {
        return cartItemRepository.findAll().stream()
                .map(cartItem -> {
                    User user = userRepository.findById(cartItem.getUser().getUserId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                    String token = generateToken(user);
                    CartItemResponse cartItemResponse = cartItemMapper.toCartItemResponse(cartItem);
                    BookResponse bookResponse = fetchBookResponse(cartItem.getBookId(), token);
                    cartItemResponse.setBookId(bookResponse);
                    return cartItemResponse;
                })
                .toList();
    }

    public CartItemResponse updateCartItem(String cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_EXISTED));
        cartItemMapper.updateCartItem(cartItem, request);
        return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem));
    }

    public UserResponse addCartItemToUser(String userId, AddCartItemRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Set<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemId())
                .stream().collect(Collectors.toSet());
        user.getCartItem().addAll(cartItems);
        userRepository.save(user);

        String token = generateToken(user);
        Set<CartItemResponse> cartItemResponse = cartItems.stream()
                .map(cartItem -> {
                    CartItemResponse itemResponse = cartItemMapper.toCartItemResponse(cartItem);
                    BookResponse bookResponse = fetchBookResponse(cartItem.getBookId(), token);
                    itemResponse.setBookId(bookResponse);
                    return itemResponse;
                }).collect(Collectors.toSet());

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setCartItem(cartItemResponse);

        return userResponse;
    }

    public UserResponse removeCartItemToUser(String userId, RemoveCartItemRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Set<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemId())
                .stream().collect(Collectors.toSet());
        user.getCartItem().removeAll(cartItems);
        userRepository.save(user);

        String token = generateToken(user);
        Set<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> {
                    CartItemResponse response = cartItemMapper.toCartItemResponse(cartItem);
                    BookResponse bookResponse = fetchBookResponse(cartItem.getBookId(), token);
                    response.setBookId(bookResponse);
                    return response;
                }).collect(Collectors.toSet());

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setCartItem(cartItemResponses);
        cartItemRepository.deleteAll(cartItems);

        return userResponse;
    }

    public void deleteCartItem(String cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(valid_duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    public BookResponse fetchBookResponse(String bookId, String token) {
        ApiResponse<BookResponse> apiResponse = bookClient.getBook(bookId, "Bearer " + token);
        return apiResponse.getResult();
    }

}
