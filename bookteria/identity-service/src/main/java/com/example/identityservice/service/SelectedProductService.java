package com.example.identityservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.SelectedProductResponse;
import com.example.identityservice.entity.SelectedProduct;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.SelectedProductMapper;
import com.example.identityservice.repository.CartItemRepository;
import com.example.identityservice.repository.OrdersRepository;
import com.example.identityservice.repository.SelectedProductRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SelectedProductService {
    SelectedProductRepository selectedProductRepository;
    OrdersRepository ordersRepository;
    CartItemRepository cartItemRepository;
    SelectedProductMapper selectedProductMapper;
    UserRepository userRepository;
    BookClient bookClient;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long valid_duration;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    public SelectedProductResponse createSelectedProduct(SelectedProductRequest request) {
        var user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        SelectedProduct selectedProduct = selectedProductMapper.toSelectedProduct(request);
        BookResponse bookResponse = fetchBookResponse(request.getBookId(), generateToken(user));
        selectedProduct.setUser(user);
        selectedProduct.setBookId(bookResponse.getBookId());
        selectedProductRepository.save(selectedProduct);

        SelectedProductResponse selectedProductResponse =
                selectedProductMapper.toSelectedProductResponse(selectedProduct);
        selectedProductResponse.setBookId(bookResponse);
        return selectedProductResponse;
    }

    public SelectedProductResponse updateSelectedProduct(String selectedId, UpdateSelectedProductRequest request) {
        SelectedProduct selectedProduct = selectedProductRepository
                .findById(selectedId)
                .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
        selectedProductMapper.updateSelectedProduct(selectedProduct, request);
        return selectedProductMapper.toSelectedProductResponse(selectedProductRepository.save(selectedProduct));
    }

    public void addSelectedProductWithUser(String orderId, AddSelectedProductRequest request) {
        var orders =
                ordersRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
        Set<SelectedProduct> selectedProducts = selectedProductRepository.findAllById(request.getSelectedId()).stream()
                .collect(Collectors.toSet());
        orders.getSelectedProducts().addAll(selectedProducts);
        ordersRepository.save(orders);
    }

    public void removeSelectedProductWithUser(String orderId, RemoveSelectedProductRequest request) {
        var orders =
                ordersRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
        Set<SelectedProduct> selectedProductSet =
                selectedProductRepository.findAllById(request.getSelectedId()).stream()
                        .collect(Collectors.toSet());
        orders.getSelectedProducts().removeAll(selectedProductSet);
        selectedProductRepository.deleteAll(selectedProductSet);
    }

    public void deleteAllSelectedProduct() {
        selectedProductRepository.deleteAll();
    }

    public SelectedProductResponse getSelectedProduct(String selectedId) {
        SelectedProduct selectedProduct = selectedProductRepository
                .findById(selectedId)
                .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
        User user = userRepository
                .findById(selectedProduct.getUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String token = generateToken(user);
        SelectedProductResponse selectedProductResponse =
                selectedProductMapper.toSelectedProductResponse(selectedProduct);
        BookResponse bookResponse = fetchBookResponse(selectedProduct.getBookId(), token);
        selectedProductResponse.setBookId(bookResponse);

        return selectedProductResponse;
    }

    public List<SelectedProductResponse> getAllSelectProduct() {
        return selectedProductRepository.findAll().stream()
                .map(selectedProduct -> {
                    SelectedProduct selectedProduct1 = selectedProductRepository
                            .findById(selectedProduct.getSelectedId())
                            .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
                    User user = userRepository
                            .findById(selectedProduct1.getUser().getUserId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                    String token = generateToken(user);
                    SelectedProductResponse selectedProductResponse =
                            selectedProductMapper.toSelectedProductResponse(selectedProduct);
                    BookResponse bookResponse = fetchBookResponse(selectedProduct.getBookId(), token);
                    selectedProductResponse.setBookId(bookResponse);

                    return selectedProductResponse;
                })
                .collect(Collectors.toList());
    }

    public void deleteSelectedProduct(String selectedId) {
        selectedProductRepository.deleteById(selectedId);
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
