package com.example.identityservice.service;

import com.example.identityservice.dto.request.ApiResponse;
import com.example.identityservice.dto.request.OrdersRequest.AddSelectedProductWithOrdersWithUserRequest;
import com.example.identityservice.dto.request.OrdersRequest.CreateOrdersRequest;
import com.example.identityservice.dto.request.OrdersRequest.RemoveSelectedProductWithOrdersWithUserRequest;
import com.example.identityservice.dto.request.OrdersRequest.UpdateOrdersRequest;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.OrdersResponse.OrdersResponse;
import com.example.identityservice.dto.request.response.SelectedProductResponse;
import com.example.identityservice.dto.request.vn_pay.VNPayDTO;
import com.example.identityservice.dto.request.vn_pay.VNPayResponseDTO;
import com.example.identityservice.entity.Orders;
import com.example.identityservice.entity.SelectedProduct;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.OrdersMapper;
import com.example.identityservice.mapper.SelectedProductMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersService {
    OrdersRepository ordersRepository;
    OrdersMapper ordersMapper;
    UserRepository userRepository;
    SelectedProductRepository selectedProductRepository;
    SelectedProductMapper selectedProductMapper;

    BookClient bookClient;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long valid_duration;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;
    //create Orders before Selected_Product
    public OrdersResponse createOrder(CreateOrdersRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var order = ordersMapper.toOrders(request);

        Set<SelectedProduct> selectedProductSet = selectedProductSet(request.getSelectedProducts());

        order.setUser(user);
        order.setSelectedProducts(selectedProductSet);
        ordersRepository.save(order);

        Set<SelectedProductResponse> selectedProductResponses = selectedProductResponses(selectedProductSet, user);

        OrdersResponse ordersResponse1 = ordersMapper.toOrdersResponse(order);
        ordersResponse1.setSelectedProducts(selectedProductResponses);

        return ordersResponse1;
    }

    public Orders createOrders(CreateOrdersRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var order = ordersMapper.toOrders(request);
        order.setUser(user);
        Set<SelectedProduct> selectedProductSet = selectedProductRepository.findAllById(request.getSelectedProducts())
                .stream().map(selectedProduct -> {
                    SelectedProduct selectedProduct1 = selectedProductRepository.findById(selectedProduct.getSelectedId())
                            .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
                    BookResponse bookResponse = fetchBookResponse(selectedProduct.getBookId(), generateToken(user));
                    double amoutTotal = 0;
                    amoutTotal += selectedProduct1.getQuantity() * bookResponse.getPrice();
                    order.setVnpAmount(BigDecimal.valueOf(amoutTotal));
                    return selectedProduct1;
                }).collect(Collectors.toSet());

        order.setSelectedProducts(selectedProductSet);
        ordersRepository.save(order);
        return ordersRepository.save(order);
    }

    public Orders getOrderId(String orderId) {
        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
        return order;
    }

    public Orders updateVNPayResponse(String orderId, VNPayResponseDTO responseDTO) {
        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));

        order.setVnpTxnRef(orderId);
        order.setVnpOrderInfo(responseDTO.getOrderInfo());
        order.setVnpResponseCode(responseDTO.getResponseCode());
        order.setVnpTransactionNo(responseDTO.getTransactionNo());
        order.setVnpPayDate(responseDTO.getPayDate());
        order.setVnpTransactionStatus(responseDTO.getTransactionStatus()); //update (19/07)

        if ("00".equals(responseDTO.getResponseCode()) && "00".equals(responseDTO.getTransactionStatus())) {
            order.setVnpTransactionStatus("PAYMENT_SUCCESS");
        } else {
            order.setVnpTransactionStatus("PAYMENT_FAILED");
        }

        return ordersRepository.save(order);
    }


    public OrdersResponse getOrders(String orderId) {
        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
        var user = userRepository.findById(order.getUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Set<SelectedProduct> selectedProducts = selectedProducts(order.getSelectedProducts());
        Set<SelectedProductResponse> selectedProductResponses = selectedProductResponses(selectedProducts, user);
        OrdersResponse ordersResponse1 = ordersMapper.toOrdersResponse(order);
        ordersResponse1.setSelectedProducts(selectedProductResponses);
        return ordersResponse1;
    }

    public Set<OrdersResponse> getAllOrders() {
        return ordersRepository.findAll().stream()
                .map(orders -> {
                    Orders orders1 = ordersRepository.findById(orders.getOrderId())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
                    OrdersResponse ordersResponse = ordersMapper.toOrdersResponse(orders1);
                    var user = userRepository.findById(orders.getUser().getUserId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                    Set<SelectedProductResponse> selectedProductResponses = selectedProductResponses(orders1.getSelectedProducts(), user);
                    ordersResponse.setSelectedProducts(selectedProductResponses);
                    return ordersResponse;
                }).collect(Collectors.toSet());
    }

    public Set<SelectedProduct> selectedProductSet (Set<String> selectedProducts) {
        Set<SelectedProduct> selectedProductSet = selectedProductRepository.findAllById(selectedProducts)
                .stream().map(selectedProduct -> {
                    SelectedProduct selectedProduct1 = selectedProductRepository.findById(selectedProduct.getSelectedId())
                            .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
                    return selectedProduct1;
                }).collect(Collectors.toSet());
        return selectedProductSet;
    };

    public Set<SelectedProduct> selectedProducts (Set<SelectedProduct> selectedProductSet) {
        Set<SelectedProduct> selectedProducts = selectedProductSet.stream()
                .map(selectedProduct -> {
                    var user = userRepository.findById(selectedProduct.getUser().getUserId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                    SelectedProduct selectedProduct1 = selectedProductRepository.findById(selectedProduct.getSelectedId())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
                    selectedProduct1.setUser(user);
                    selectedProduct1 = selectedProductRepository.save(selectedProduct);
                    return selectedProduct1;
                }).collect(Collectors.toSet());
        return selectedProducts;
    }

    public Set<SelectedProductResponse> selectedProductResponses (Set<SelectedProduct> selectedProducts, User user) {
        Set<SelectedProductResponse> selectedProductResponses = selectedProducts.stream()
                .map(selectedProduct -> {
                    SelectedProductResponse selectedProductResponse = selectedProductMapper.toSelectedProductResponse(selectedProduct);
                    BookResponse bookResponse = fetchBookResponse(selectedProduct.getBookId(), generateToken(user));
                    selectedProductResponse.setBookId(bookResponse);
                    return selectedProductResponse;
                }).collect(Collectors.toSet());
        return selectedProductResponses;
    };

    public OrdersResponse updateOrders(String orderId, UpdateOrdersRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
        ordersMapper.updateOrder(order, request);
        return ordersMapper.toOrdersResponse(ordersRepository.save(order));
    }

    public void addSelectedProductWithOrdersWithUser(String userId, AddSelectedProductWithOrdersWithUserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Set<Orders> orders = ordersRepository.findAllById(request.getOrderId())
                .stream().collect(Collectors.toSet());
        user.getOrders().addAll(orders);
        userRepository.save(user);
    }

    public void removeSelectedProductWithOrdersWithUser(String userId, RemoveSelectedProductWithOrdersWithUserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Set<Orders> orders = ordersRepository.findAllById(request.getOrderId())
                .stream().collect(Collectors.toSet());
        user.getOrders().removeAll(orders);
        ordersRepository.deleteAll(orders);
    }

    public void deleteOrders(String orderId) {
        ordersRepository.deleteById(orderId);
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
