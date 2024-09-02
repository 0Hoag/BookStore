package com.example.identityservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import com.example.identityservice.dto.request.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PermissionControllerTest {

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
    private PermissionService permissionService;

    private PermissionRequest request;

    private PermissionResponse response;

    @BeforeEach
    void initData() throws Exception {

        request = PermissionRequest.builder()
                .name("APPROVE_POST")
                .description("APPROVE_POST Data Permission")
                .build();

        response = PermissionResponse.builder()
                .name("APPROVE_POST")
                .description("APPROVE_POST Data Permission")
                .build();
    }

    @Test
    @WithMockUser
    void createPermission_validRequest_success() throws Exception {

        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String result = objectMapper.writeValueAsString(request);

        // WHEN
        Mockito.when(permissionService.create(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/permissions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.name").value("APPROVE_POST"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.description").value("APPROVE_POST Data Permission"))
                .andReturn();
    }

    @Test
    @WithMockUser
    void getAllPermission_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN,THEN

        Mockito.when(permissionService.getALL()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/permissions")
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
        String permissionName = "UPDATE_DATA";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String result = objectMapper.writeValueAsString(request);
        // WHEN

        mockMvc.perform(delete("/permissions/" + permissionName)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("permission has been delete"));
        // THEN
    }
}
