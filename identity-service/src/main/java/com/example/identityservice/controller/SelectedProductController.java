package com.example.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.SelectedProductResponse;
import com.example.identityservice.service.SelectedProductService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/selectProduct")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SelectedProductController {
    SelectedProductService selectedProductService;

    @PostMapping("/registration")
    public ApiResponse<SelectedProductResponse> createSelectedProduct(@RequestBody SelectedProductRequest request) {
        return ApiResponse.<SelectedProductResponse>builder()
                .code(1000)
                .result(selectedProductService.createSelectedProduct(request))
                .build();
    }

    @PutMapping("/updateSelectedProduct/{selectedId}")
    public ApiResponse<SelectedProductResponse> updateSelectedProduct(
            @PathVariable String selectedId, @RequestBody UpdateSelectedProductRequest request) {
        return ApiResponse.<SelectedProductResponse>builder()
                .code(1000)
                .result(selectedProductService.updateSelectedProduct(selectedId, request))
                .build();
    }

    @PostMapping("/addSelectedProductWithUser/{orderId}")
    public ApiResponse<Void> addSelectedProductWithUser(
            @PathVariable String orderId, @RequestBody AddSelectedProductRequest request) {
        selectedProductService.addSelectedProductWithUser(orderId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Add SelectedProduct Success")
                .build();
    }

    @DeleteMapping("/removeSelectedProductWithUser/{orderId}")
    public ApiResponse<Void> removeSelectedProductWithUser(
            @PathVariable String orderId, @RequestBody RemoveSelectedProductRequest request) {
        selectedProductService.removeSelectedProductWithUser(orderId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Remove SelectedProduct Success")
                .build();
    }

    @DeleteMapping("/deleteSelectedAll")
    public ApiResponse<Void> deleteSelectedAll() {
        selectedProductService.deleteAllSelectedProduct();
        return ApiResponse.<Void>builder().code(1000).message("Delete All").build();
    }

    @GetMapping("/{SelectedId}")
    public ApiResponse<SelectedProductResponse> getSelectedProduct(@PathVariable String SelectedId) {
        return ApiResponse.<SelectedProductResponse>builder()
                .code(1000)
                .result(selectedProductService.getSelectedProduct(SelectedId))
                .build();
    }

    @GetMapping("/GetAllSelectedProduct")
    public ApiResponse<List<SelectedProductResponse>> getAllSelectedProduct() {
        return ApiResponse.<List<SelectedProductResponse>>builder()
                .code(1000)
                .result(selectedProductService.getAllSelectProduct())
                .build();
    }

    @DeleteMapping("/{SelectedId}")
    public ApiResponse<SelectedProductResponse> deleteSelectedProduct(@PathVariable String SelectedId) {
        selectedProductService.deleteSelectedProduct(SelectedId);
        return ApiResponse.<SelectedProductResponse>builder()
                .code(1000)
                .message("Delete selectedProduct success")
                .build();
    }
}
