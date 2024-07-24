package com.example.identityservice.mapper;

import com.example.identityservice.dto.request.CartItemRequest;
import com.example.identityservice.dto.request.UpdateCartItemRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.CartItemResponse;
import com.example.identityservice.entity.CartItem;
import com.example.identityservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mappings({
            @Mapping(target = "bookId", ignore = true)
    })
    CartItem toCartItem(CartItemRequest request);

    @Mappings({
            @Mapping(source = "bookId", target = "bookId", qualifiedByName = "mapBookIdToBookResponse")
    })
    CartItemResponse toCartItemResponse(CartItem cartItem);

    void updateCartItem(@MappingTarget CartItem cartItem, UpdateCartItemRequest request);

    @Named("mapBookIdToBookResponse")
    default BookResponse mapBookIdToBookResponse(String bookId) {
        return BookResponse.builder()
                .bookId(bookId)
                .build();
    }
}

