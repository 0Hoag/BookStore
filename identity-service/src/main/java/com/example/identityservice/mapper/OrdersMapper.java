package com.example.identityservice.mapper;

import org.mapstruct.*;

import com.example.identityservice.dto.request.OrdersRequest.CreateOrdersRequest;
import com.example.identityservice.dto.request.OrdersRequest.UpdateOrdersRequest;
import com.example.identityservice.dto.request.response.BookResponse;
import com.example.identityservice.dto.request.response.OrdersResponse.OrdersResponse;
import com.example.identityservice.dto.request.response.SelectedProductResponse;
import com.example.identityservice.entity.Orders;

@Mapper(componentModel = "spring")
public interface OrdersMapper {

    @Mapping(target = "selectedProducts", ignore = true)
    Orders toOrders(CreateOrdersRequest request);

    @Mapping(target = "selectedProducts", ignore = true)
    OrdersResponse toOrdersResponse(Orders orders);

    void updateOrder(@MappingTarget Orders orders, UpdateOrdersRequest request);

    @Named("mapBookIdToBookResponse")
    default BookResponse mapBookIdToBookResponse(String bookId) {
        return BookResponse.builder().bookId(bookId).build();
    }

    // You may need to adjust or remove this mapping based on your entity structure
    @Named("mapSelectedProductResponse")
    default SelectedProductResponse mapSelectedProductResponse(String selectedId) {
        return SelectedProductResponse.builder().selectedId(selectedId).build();
    }
}
