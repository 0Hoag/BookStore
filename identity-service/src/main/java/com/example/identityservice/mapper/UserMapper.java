package com.example.identityservice.mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.example.identityservice.dto.request.UpdateInformationRequest;
import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.entity.*;

@Mapper(
        componentModel = "spring",
        uses = {CartItemMapper.class, SelectedProductMapper.class})
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mappings({@Mapping(source = "cartItem", target = "cartItem")})
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateInformationUser(@MappingTarget User user, UpdateInformationRequest request);

    default Set<CartItem> map(Collection<String> cartItemId) {
        return cartItemId.stream()
                .map(id -> CartItem.builder().cartItemId(id).build())
                .collect(Collectors.toSet());
    }

    default Set<SelectedProduct> mapSelectedProducts(Set<String> selectedProductId) {
        return selectedProductId.stream()
                .map(id -> SelectedProduct.builder().selectedId(id).build())
                .collect(Collectors.toSet());
    }

    default Set<Orders> mapOrders(Set<String> orderId) {
        return orderId.stream().map(id -> Orders.builder().orderId(id).build()).collect(Collectors.toSet());
    }

    default Set<String> mapUserImageId(Set<UserImage> images) {
        return images.stream().map(userImage -> {
            return userImage.getImageId();
        }).collect(Collectors.toSet());
    }
}
