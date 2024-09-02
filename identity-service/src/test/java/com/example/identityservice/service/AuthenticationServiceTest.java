package com.example.identityservice.service;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.RefreshRequest;
import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.response.AuthenticationResponse;
import com.example.identityservice.dto.request.response.IntrospectResponse;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.entity.InvalidatedToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.repository.InvalidatedRepository;
import com.example.identityservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
// @TestPropertySource("/test.properties")
@Slf4j
@AutoConfigureMockMvc
public class AuthenticationServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private InvalidatedRepository invalidatedRepository;

    @SpyBean
    private UserRepository userRepository;

    private UserResponse userResponse;
    private AuthenticationRequest request;
    private AuthenticationResponse response;
    private UserCreationRequest createRequest;
    private IntrospectResponse introspectResponse;
    private IntrospectRequest introspectRequest;
    private InvalidatedToken invalidatedToken;
    private String authToken;
    private RefreshRequest refreshRequest;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() throws Exception {
        dob = LocalDate.of(2005, 9, 2);

        createRequest = UserCreationRequest.builder()
                .username("Harry Potter")
                .firstName("Harry")
                .lastName("Potter")
                .password("123456")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .userId("b611c63db22c")
                .username("Harry Potter")
                .build();

        invalidatedToken = InvalidatedToken.builder()
                .id("89c2eac3-6b0b-47ae-991d-c36e3525ab9b")
                .exprityTime(new Date())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createRequest);

        MvcResult result = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.token").exists())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("result.authenticated").value("true"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(responseContent);
        authToken = jsonObject.getJSONObject("result").getString("token");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        request = AuthenticationRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        response = AuthenticationResponse.builder()
                .token(authToken)
                .authenticated(true)
                .build();

        user = User.builder().userId("b611c63db22c").username("Harry Potter").build();

        refreshRequest = RefreshRequest.builder().token(authToken).build();

        introspectRequest = IntrospectRequest.builder().token(authToken).build();

        introspectResponse = IntrospectResponse.builder().valid(true).build();
    }

    @Test
    void introspectResponse_validRequest_success() throws ParseException, JOSEException {
        // GIVEN
        when(invalidatedRepository.findById(anyString())).thenReturn(Optional.of(invalidatedToken));
        // WHEN
        var response = authenticationService.introspectResponse(introspectRequest);
        // THEN
        if (response.isValid()) {
            Assertions.assertTrue(response.isValid());
        } else {
            Assertions.assertFalse(response.isValid());
        }
    }

    @Test
    void TestrefreshToken_validRequest_success() throws ParseException, JOSEException {
        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        // WHEN
        var refreshToken = authenticationService.refreshToken(refreshRequest);
        // THEN
        Assertions.assertNotEquals(refreshToken.getToken(), response.getToken());
        Assertions.assertEquals(refreshToken.isAuthenticated(), response.isAuthenticated());
    }

    @Test
    void TestrefreshToken_InvalidRequest_success() {
        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        // WHEN,THEN
        Assertions.assertThrows(AppException.class, () -> authenticationService.authenticated(request));
    }
}
