package com.example.identityservice.mapper;

import com.example.identityservice.dto.request.CartItemRequest;
import com.example.identityservice.dto.request.SelectedProductRequest;
import com.example.identityservice.dto.request.UpdateCartItemRequest;
import com.example.identityservice.dto.request.UpdateSelectedProductRequest;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.CartItemResponse;
import com.example.identityservice.dto.request.response.SelectedProductResponse;
import com.example.identityservice.entity.CartItem;
import com.example.identityservice.entity.SelectedProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SelectedProductMapper {
    @Mappings({
            @Mapping(target = "bookId", ignore = true)
    })
    SelectedProduct toSelectedProduct(SelectedProductRequest request);

    @Mappings({
            @Mapping(source = "bookId", target = "bookId", qualifiedByName = "mapBookIdToBookResponse")
    })
    SelectedProductResponse toSelectedProductResponse(SelectedProduct selectedProduct);

    void updateSelectedProduct(@MappingTarget SelectedProduct selectedProduct, UpdateSelectedProductRequest request);

    @Named("mapBookIdToBookResponse")
    default BookResponse mapBookIdToBookResponse(String bookId) {
        return BookResponse.builder()
                .bookId(bookId)
                .build();
    }
}
