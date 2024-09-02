package com.example.identityservice.service;

import static org.mockito.Mockito.*;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.request.response.PermissionResponse;
import com.example.identityservice.dto.request.response.RoleResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.entity.Role;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepositoty;

@SpringBootTest
@TestPropertySource("/test.properties")
class RoleServiceTest {
    @Autowired
    RoleService roleService;

    @MockBean
    RoleRepositoty roleRepositoty;

    @SpyBean
    PermissionRepository permissionRepository;

    private RoleRequest request;
    private RoleResponse response;
    private Set<PermissionResponse> permissionResponses;
    private Set<String> permission;
    private Set<Permission> permissions;
    private Role role;

    @BeforeEach
    void initData() {
        permission = new HashSet<>();
        permission.add("USER");

        permissions = new HashSet<>();
        Permission permission1 = new Permission("CREATE_DATA", "CREATE Data Permission");
        permissions.add(permission1);

        request = RoleRequest.builder()
                .name("USER")
                .description("USER ROLE")
                .permissions(permission)
                .build();

        role = Role.builder()
                .name("USER")
                .description("USER ROLE")
                .permissions(permissions)
                .build();
    }

    //        @Test
    //        void createRole_validRequest_success() {
    //            //GIVEN
    //            when(permissionRepository.findAllById(any())).thenReturn((List<Permission>) permissions);
    //            //WHEN
    //
    //            //THEN
    //        }

    @Test
    void getAllRole_validRequest_success() {
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        when(roleRepositoty.findAll()).thenReturn(roles);

        var response = roleService.getAll();

        Assertions.assertThat(response.toArray()).isNotEmpty();
    }

    @Test
    void deleteRole_validRequest_success() {
        // GIVEN
        String roleName = role.getName();
        when(roleRepositoty.findById(role.getName())).thenReturn(Optional.of(role));
        // WHEN
        roleService.delete(role.getName());
        // THEM
        verify(roleRepositoty).deleteById(roleName);
    }
}
