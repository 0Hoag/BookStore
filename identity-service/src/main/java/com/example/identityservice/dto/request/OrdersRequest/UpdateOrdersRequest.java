package com.example.identityservice.dto.request.OrdersRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrdersRequest {
    String fullName;
    String phoneNumber;
    String country;
    String city;
    String district;
    String ward;
    String address;
    String paymentMethod;

    String userId;
}
