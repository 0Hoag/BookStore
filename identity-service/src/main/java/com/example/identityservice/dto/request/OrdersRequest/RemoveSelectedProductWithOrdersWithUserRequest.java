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
public class RemoveSelectedProductWithOrdersWithUserRequest {
    Set<String> orderId;
}
