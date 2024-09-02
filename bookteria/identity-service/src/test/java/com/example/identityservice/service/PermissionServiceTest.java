package com.example.identityservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.request.response.PermissionResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.repository.PermissionRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
class PermissionServiceTest {
    @Autowired
    private PermissionService permissionService;

    @MockBean
    private PermissionRepository repository;

    private PermissionRequest request;

    private PermissionResponse response;

    private Permission permission;

    @BeforeEach
    void initData() {
        request = PermissionRequest.builder()
                .name("CREATE_DATA")
                .description("CREATE Data Permission")
                .build();

        response = PermissionResponse.builder()
                .name("CREATE_DATA")
                .description("CREATE Data Permission")
                .build();

        permission = Permission.builder()
                .name("CREATE_DATA")
                .description("CREATE Data Permission")
                .build();
    }

    @Test
    void createPermission_validRequest_success() {
        // GIVEN
        when(repository.findById(anyString())).thenReturn(Optional.of(permission));
        when(repository.save(any())).thenReturn(permission);
        // WHEN
        var response = permissionService.create(request);
        // THEN
        Assertions.assertThat(response.getName()).isEqualTo(permission.getName());
        Assertions.assertThat(response.getDescription()).isEqualTo(permission.getDescription());
    }

    @Test
    void getAllPermission_validRequest_success() {
        List<Permission> Permission = new ArrayList<>();
        Permission.add(permission);
        when(repository.findAll()).thenReturn(Permission);

        var response = permissionService.getALL();

        Assertions.assertThat(response.toArray()).isNotEmpty();
    }

    @Test
    void deletePermission_validRequest_success() {
        String userId = permission.getName();
        when(repository.findById(userId)).thenReturn(Optional.of(permission));
        // WHEN
        permissionService.delete(permission.getName());
        // THEM
        verify(repository).deleteById(userId);
    }
}
