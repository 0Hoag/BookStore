package com.example.notification.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.response.UserProfileResponse;
import com.example.notification.service.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private ProfileCreationRequest request;

    private UserProfileResponse response;

    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2005, 9, 2);

        request = ProfileCreationRequest.builder()
                .userId("9cf79d3e-049f-427b-8286-78923f2e9c2f")
                .firstName("Harry")
                .lastName("Potter")
                .dob(dob)
                .city("Ho Chi Minh")
                .build();

        response = UserProfileResponse.builder()
                .id("641241c7-59b7-420e-b18e-c3b92a3c6752")
                .firstName("Harry")
                .lastName("Potter")
                .dob(dob)
                .city("Ho Chi Minh")
                .build();
    }

    @Test
    @WithMockUser
    void getProfile_validRequest_success() throws Exception {
        String id = "641241c7-59b7-420e-b18e-c3b92a3c6752";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        when(userProfileService.getProfile(anyString())).thenReturn(response);

        mockMvc.perform(get("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("641241c7-59b7-420e-b18e-c3b92a3c6752"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value("Harry"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value("Potter"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.dob").value("2005-09-02"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.city").value("Ho Chi Minh"));
    }

    @Test
    @WithMockUser
    void getAllProfile_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN
        when(userProfileService.getAllProfile()).thenReturn(asList());
        // THEN
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray());
    }

    @Test
    @WithMockUser
    void deleteProfile_validRequest_success() throws Exception {
        // GIVEN
        String id = "641241c7-59b7-420e-b18e-c3b92a3c6752";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN, THEN
        mockMvc.perform(delete("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Delete Profile Success"));
    }

    @Test
    @WithMockUser
    void deleteProfileUserId_validRequest_success() throws Exception {
        // GIVEN
        String userid = "9cf79d3e-049f-427b-8286-78923f2e9c2f";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN, THEN
        mockMvc.perform(delete("/users/userId/" + userid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Delete Profile to UserId Success"));
    }

    @Test
    @WithMockUser
    void updateProfile_validRequest_success() throws Exception {
        // GIVEN
        String id = "641241c7-59b7-420e-b18e-c3b92a3c6752";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);
        // WHEN
        when(userProfileService.updateProfile(eq(id), any())).thenReturn(response);
        // THEN
        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("641241c7-59b7-420e-b18e-c3b92a3c6752"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value("Harry"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value("Potter"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.dob").value("2005-09-02"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.city").value("Ho Chi Minh"));
    }
}
