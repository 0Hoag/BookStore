package com.example.identityservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.identityservice.dto.request.PermissionRequest;
import com.example.identityservice.dto.request.RoleRequest;
import com.example.identityservice.dto.request.response.PermissionResponse;
import com.example.identityservice.dto.request.response.RoleResponse;
import com.example.identityservice.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RoleControllerTest {

    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    private RoleRequest roleRequest;

    private RoleResponse roleResponse;

    @BeforeEach
    void initData() {
        Set<String> permissionRequest = new HashSet<>();
        PermissionRequest permissionRequest1 = new PermissionRequest("CREATE_DATA", "CREATE Data Permission");
        permissionRequest.add(String.valueOf(permissionRequest1));

        Set<PermissionResponse> permissionResponses = new HashSet<>();
        PermissionResponse permissionResponse1 = new PermissionResponse("CREATE_DATA", "CREATE Data Permission");
        permissionResponses.add(permissionResponse1);

        roleRequest = RoleRequest.builder()
                .name("USER")
                .description("USER ROLE")
                .permissions(permissionRequest)
                .build();

        roleResponse = RoleResponse.builder()
                .name("USER")
                .description("CREATE Data Permission")
                .permissions(permissionResponses)
                .build();
    }

    @Test
    @WithMockUser
    void createRole_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String result = objectMapper.writeValueAsString(roleRequest);

        Mockito.when(roleService.create(any())).thenReturn(roleResponse);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.name").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.description").value("CREATE Data Permission"))
                .andReturn();
    }

    @Test
    @WithMockUser
    void getAllRole_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(roleRequest);
        // WHEN,THEN

        Mockito.when(roleService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/roles")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray());
    }

    @Test
    @WithMockUser
    void deletePermission_validRequest_success() throws Exception {
        // GIVEn
        String roleName = "USER";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String result = objectMapper.writeValueAsString(roleRequest);
        // WHEN

        mockMvc.perform(delete("/roles/" + roleName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("role has been delete"));
        // THEN
    }
}
