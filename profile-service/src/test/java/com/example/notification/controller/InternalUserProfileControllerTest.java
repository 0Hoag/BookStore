package com.example.notification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.response.UserProfileResponse;
import com.example.notification.service.UserProfileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class InternalUserProfileControllerTest {

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

//    @Test
//    @WithMockUser
//    void createProfile_validRequest_success() throws Exception {
//        // GIVEN
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        String content = objectMapper.writeValueAsString(request);
//
//        when(userProfileService.createProfile(any())).thenReturn(response);
//        // WHEN
//        mockMvc.perform(post("/internal/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(content))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value("Harry"))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value("Potter"))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.dob").value("2005-09-02"))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.city").value("Ho Chi Minh"));
//        // THEn
//    }
}
