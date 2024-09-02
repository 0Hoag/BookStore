package com.example.identityservice.dto.request.OrdersRequest;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrdersRequest {
    String fullName;
    String phoneNumber;
    String country;
    String city;
    String district;
    String ward;
    String address;
    String paymentMethod;
    Set<String> selectedProducts;
    String userId;
    // them vao total
}
