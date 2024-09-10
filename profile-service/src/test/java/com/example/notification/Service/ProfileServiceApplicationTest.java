package com.example.notification.Service;

import static com.example.notification.exception.ErrorCode.PROFILE_NOT_EXISTED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.response.UserProfileResponse;
import com.example.notification.entity.UserProfile;
import com.example.notification.exception.AppException;
import com.example.notification.mapper.UserProfileMapper;
import com.example.notification.repository.UserProfileRepository;
import com.example.notification.service.UserProfileService;

@SpringBootTest
@TestPropertySource("/test.properties")
public class ProfileServiceApplicationTest {

    @Autowired
    private UserProfileService service;

    @MockBean
    private UserProfileRepository userProfileRepository;

    private ProfileCreationRequest request;

    private UserProfileResponse response;

    private UserProfile userProfile;

    private UserProfileMapper userProfileMapper;

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

        userProfile = UserProfile.builder()
                .id("641241c7-59b7-420e-b18e-c3b92a3c6752")
                .userId("9cf79d3e-049f-427b-8286-78923f2e9c2f")
                .firstName("Harry")
                .lastName("Potter")
                .dob(dob)
                .city("Ho Chi Minh")
                .build();
    }

    @Test
    void createProfile_validRequest_success() {
        // GIVEN
        when(userProfileRepository.save(any())).thenReturn(userProfile);
        // WHEN
        var response = service.createProfile(request);
        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("641241c7-59b7-420e-b18e-c3b92a3c6752");
        Assertions.assertThat(response.getFirstName()).isEqualTo("Harry");
        Assertions.assertThat(response.getLastName()).isEqualTo("Potter");
        Assertions.assertThat(response.getDob()).isEqualTo("2005-09-02");
        Assertions.assertThat(response.getCity()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void getProfile_validRequest_success() {
        // GIVEN
        when(userProfileRepository.findById(anyString())).thenReturn(Optional.of(userProfile));
        // WHEN
        var response = service.getProfile(String.valueOf(Optional.of(userProfile)));
        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("641241c7-59b7-420e-b18e-c3b92a3c6752");
        Assertions.assertThat(response.getFirstName()).isEqualTo("Harry");
        Assertions.assertThat(response.getLastName()).isEqualTo("Potter");
        Assertions.assertThat(response.getDob()).isEqualTo("2005-09-02");
        Assertions.assertThat(response.getCity()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void getProfile_InvalidRequest_success() {
        // GIVEN
        when(userProfileRepository.existsById(anyString())).thenReturn(true);
        // WHEN
        var exception = assertThrows(AppException.class, () -> service.getProfile(anyString()));
        // THEN
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(PROFILE_NOT_EXISTED);
        Assertions.assertThat(exception.getMessage()).isEqualTo("USERID_NOT_EXISTED");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProfile_validRequest_success() {
        // GIVEN
        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(userProfile);
        when(userProfileRepository.findAll()).thenReturn(profiles);
        // WHEN
        var response = service.getAllProfile();
        // THEN
        Assertions.assertThat(response.toArray()).isNotEmpty();
    }

    @Test
    void deleteProfile_validRequest_success() {
        // GIVEN
        when(userProfileRepository.findById(anyString())).thenReturn(Optional.of(userProfile));
        // WHEN
        service.deleteProfile(anyString());
        // THEN
        verify(userProfileRepository).deleteById(anyString());
    }
}
