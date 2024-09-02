package com.example.identityservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class userControllerTest {

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
    private UserService userService;

    private UserCreationRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;
    private LocalDate dob;
    private String authToken;

    @BeforeEach
    void initData() throws Exception {
        dob = LocalDate.of(2005, 9, 2);

        List<String> userRoles = Arrays.asList("USER");

        createRequest = UserCreationRequest.builder()
                .username("Harry Potter")
                .firstName("Harry")
                .lastName("Potter")
                .password("123456")
                .dob(dob)
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("123456")
                .dob(dob)
                .roles(userRoles)
                .build();

        userResponse = UserResponse.builder()
                .userId("b611c63db22c")
                .username("Harry Potter")
                .build();

        //        ObjectMapper objectMapper = new ObjectMapper();
        //        objectMapper.registerModule(new JavaTimeModule());
        //        String content = objectMapper.writeValueAsString(createRequest);
        //
        //        MvcResult result = mockMvc.perform(post("/auth/token")
        //                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        //                        .content(content))
        //                .andExpect(status().isOk())
        //                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
        //                .andExpect(MockMvcResultMatchers.jsonPath("result.token").exists())
        //                .andExpect(
        //                        MockMvcResultMatchers.jsonPath("result.authenticated").value("true"))
        //                .andReturn();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);
        // WHEN, THEN

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("b611c63db22c"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Harry Potter"));
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // GIVEN
        createRequest.setUsername("Ha");
        ObjectMapper objectMapper = new ObjectMapper();
        // after add Libary create registerModule it beacause system it passed
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        // WHEN, THEN
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("USERNAME MUST AT LEES THAN 4 CHARACTER"));
    }

    @Test
    @WithMockUser
    void getAllUser_valid_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);
        // WHEN,THEN

        when(userService.getAllUser()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray());
    }

    @Test
    @WithMockUser
    void getUser_valid_success() throws Exception {
        // GIVEN
        String userId = "3ea2b50c-6da2-4af6-8c28-b611c63db22c";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        when(userService.getUser(eq(userId))).thenReturn(userResponse);

        mockMvc.perform(get("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("b611c63db22c"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Harry Potter"));
    }

    @Test
    @WithMockUser
    void getMyInfo_valid_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        when(userService.getMyInfo()).thenReturn(userResponse);

        mockMvc.perform(get("/users/myInfo")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("b611c63db22c"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Harry Potter"));
    }

    @Test
    @WithMockUser
    void updateUser_valid_success() throws Exception {
        String userId = "b611c63db22c";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(userService.updateUser(eq(userId), any())).thenReturn(userResponse);

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Harry Potter"));
    }

    @Test
    @WithMockUser
    void deleteUser_valid_success() throws Exception {
        String userId = "3ea2b50c-6da2-4af6-8c28-b611c63db22c";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        mockMvc.perform(delete("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User has been delete"));
    }

    //    @Test
    //    void Token_validRequest_success() throws Exception {
    //        // GIVEN
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        objectMapper.registerModule(new JavaTimeModule());
    //        String content = objectMapper.writeValueAsString(createRequest);
    //
    //        MvcResult result = mockMvc.perform(post("/auth/token")
    //                        .contentType(MediaType.APPLICATION_JSON_VALUE)
    //                        .content(content))
    //                .andExpect(status().isOk())
    //                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
    //                .andExpect(MockMvcResultMatchers.jsonPath("result.token").exists())
    //                .andExpect(
    //                        MockMvcResultMatchers.jsonPath("result.authenticated").value("true"))
    //                .andReturn();

    //        String responseContent = result.getResponse().getContentAsString();
    //        JSONObject jsonObject = new JSONObject(responseContent);
    //        authToken = jsonObject.getJSONObject("result").getString("token");
    //        System.out.println("authToken: " + authToken);
    //    }
}
