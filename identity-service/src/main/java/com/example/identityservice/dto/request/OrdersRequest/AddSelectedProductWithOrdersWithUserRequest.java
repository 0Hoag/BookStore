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
public class AddSelectedProductWithOrdersWithUserRequest {
    Set<String> orderId;
}
