package com.example.identityservice.dto.request.response;

import java.util.Set;

import com.example.identityservice.dto.request.response.OrdersResponse.OrdersResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;

    //    String image; // update 09/16
    String username;
    String firstName;
    String lastName;
    Boolean noPassword;

    String email;
    boolean emailVerified;

    Set<RoleResponse> roles;
    Set<CartItemResponse> cartItem;
    Set<OrdersResponse> orders;
    Set<String> images;
}
