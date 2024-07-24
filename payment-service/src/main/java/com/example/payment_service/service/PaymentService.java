package com.example.payment_service.service;

import com.example.payment_service.dto.request.AuthenticationRequest;
import com.example.payment_service.dto.request.CreatePaymentRequest;
import com.example.payment_service.dto.request.UpdatePaymentRequest;
import com.example.payment_service.dto.response.*;
import com.example.payment_service.entity.Payment;
import com.example.payment_service.entity.enumclass.PaymentMethod;
import com.example.payment_service.exception.AppException;
import com.example.payment_service.exception.ErrorCode;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.repository.httpClient.BookClient;
import com.example.payment_service.repository.httpClient.IdentityClient;
import com.example.payment_service.repository.httpClient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentService {
    PaymentMapper paymentMapper;
    PaymentRepository paymentRepository;
    IdentityClient identityClient;
    ProfileClient profileClient;
    BookClient bookClient;

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        String token = getTokenFromIdentityService();
        var payment = paymentMapper.toPayment(request);

        //if PaymentMethod not COD
        if (payment.getPaymentMethod() != PaymentMethod.COD) {
            payment.setPaymentDate(LocalDateTime.now()) ;
        }

        payment = paymentRepository.save(payment);

        OrdersResponse ordersResponse = fetchOrder(payment.getOrderId(), token);

        Set<SelectedProductResponse> selectedProductResponses = ordersResponse.getSelectedProducts()
                .stream().map(selectedProductResponse -> {
                    SelectedProductResponse selectedProductResponse1 = fetchSelectedProduct(selectedProductResponse.getSelectedId(), token);
                    BookResponse bookResponse = fetchBookInformation(selectedProductResponse1.getBookId().getBookId(), token);
                    selectedProductResponse1.setBookId(bookResponse);
                    return selectedProductResponse;
                }).collect(Collectors.toSet());

        ordersResponse.setSelectedProducts(selectedProductResponses);

        PaymentResponse paymentRespose = paymentMapper.toPaymentResponse(payment);
        paymentRespose.setOrderId(ordersResponse);

        return paymentRespose;
    }

    public PaymentResponse getPayment(String paymentId) {
        String token = getTokenFromIdentityService();
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        OrdersResponse ordersResponse = fetchOrder(payment.getOrderId(), token);

        Set<SelectedProductResponse> selectedProductResponses = ordersResponse.getSelectedProducts()
                .stream().map(selectedProductResponse -> {
                    SelectedProductResponse selectedProductResponse1 = fetchSelectedProduct(selectedProductResponse.getSelectedId(), token);
                    BookResponse bookResponse = fetchBookInformation(selectedProductResponse1.getBookId().getBookId(), token);
                    selectedProductResponse1.setBookId(bookResponse);
                    return selectedProductResponse1;
                }).collect(Collectors.toSet());

        ordersResponse.setSelectedProducts(selectedProductResponses);

        PaymentResponse paymentRespose = paymentMapper.toPaymentResponse(payment);
        paymentRespose.setOrderId(ordersResponse);

        return paymentRespose;
    }

    public Set<PaymentResponse> getAllPayment() {
        String token = getTokenFromIdentityService();
        return paymentRepository.findAll()
                .stream().map(payment -> {
                    PaymentResponse paymentRespose = paymentMapper.toPaymentResponse(payment);
                    OrdersResponse ordersResponse = fetchOrder(paymentRespose.getOrderId().getOrderId(), token);

                    Set<SelectedProductResponse> selectedProductResponses = ordersResponse.getSelectedProducts()
                            .stream().map(selectedProductResponse -> {
                                SelectedProductResponse selectedProductResponse1 = fetchSelectedProduct(selectedProductResponse.getSelectedId(), token);
                                BookResponse bookResponse = fetchBookInformation(selectedProductResponse1.getBookId().getBookId(), token);
                                selectedProductResponse1.setBookId(bookResponse);
                                return selectedProductResponse1;
                            }).collect(Collectors.toSet());

                    ordersResponse.setSelectedProducts(selectedProductResponses);
                    paymentRespose.setOrderId(ordersResponse);
                    return paymentRespose;
                }).collect(Collectors.toSet());
    }

    public Set<PaymentResponse> getPaymenByUserId(String userId) {
        Set<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toSet());
    }

    public void deletePayment(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    public PaymentResponse updatePayment(String paymentId, UpdatePaymentRequest request) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        paymentMapper.updatePayment(payment, request);
        payment = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(payment);
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

    private OrdersResponse fetchOrder(String orderId, String token) {
        ApiResponse<OrdersResponse> ordersResponseApiResponse = identityClient.getOrder(orderId, "Bearer " + token);
        return ordersResponseApiResponse.getResult();
    }

    private SelectedProductResponse fetchSelectedProduct(String SelectedId, String token) {
        ApiResponse<SelectedProductResponse> selectedProductResponseApiResponse = identityClient.getSelectedProduct(SelectedId, "Bearer " + token);
        return selectedProductResponseApiResponse.getResult();
    }
}
