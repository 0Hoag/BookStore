package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.UserCreationRequest;
import com.example.identityservice.dto.request.UserUpdateRequest;
import com.example.identityservice.dto.request.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.RoleRepositoty;
import com.example.identityservice.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
class userServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserMapper userMapper;
    private UserCreationRequest request;
    private UserUpdateRequest updateRequest;
    private UserResponse response;
    private RoleRepositoty roleRepositoty;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(2005, 9, 2);

        request = UserCreationRequest.builder()
                .username("Harry Potter")
                .firstName("Harry")
                .lastName("Potter")
                .password("123456")
                .dob(dob)
                .build();

        response = UserResponse.builder()
                .id("b611c63db22c")
                .username("Harry Potter")
                .build();

        user = User.builder().userId("b611c63db22c").username("Harry Potter").build();

        updateRequest = UserUpdateRequest.builder().password("123456").build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("b611c63db22c");
        Assertions.assertThat(response.getUsername()).isEqualTo("Harry Potter");
    }

    @Test
    void createUser_userExisted_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        // then write test exception
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
        Assertions.assertThat(exception.getErrorCode().getMessage()).isEqualTo("USER EXITED");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUser_validRequest_success() {
        // GIVEN
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll()).thenReturn(users);

        var response = userService.getAllUser();

        Assertions.assertThat(response.toArray()).isNotEmpty();
    }

    @Test
    @WithMockUser
    void getMyInfo_validRequest_success() {
        // GIVEN
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        // WHEN
        var userResponse = userService.getMyInfo();
        // THEN
        Assertions.assertThat(userResponse.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(userResponse.getId()).isEqualTo(user.getUserId());
    }

    @Test
    @WithMockUser
    void getMyInfo_InvalidRequest_error() {
        // GIVEN
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.ofNullable(null));
        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
        Assertions.assertThat(exception.getErrorCode().getMessage())
                .isEqualTo(String.valueOf(ErrorCode.USER_NOT_EXISTED));
        Assertions.assertThat(exception.getErrorCode().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    //        @Test
    //        void updateUser_validRequest_success() {
    //            //GIVEN
    //            when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    //            when(userRepository.save(user));
    //            //WHEN
    //            userService.updateUser(anyString(), updateRequest);
    //            //THEN
    //            Assertions.assertThat(response.getFirstName()).isEqualTo(updateRequest.getFirstName());
    //            Assertions.assertThat(response.getLastName()).isEqualTo(updateRequest.getLastName());
    //            Assertions.assertThat(response.getDob()).isEqualTo(updateRequest.getDob());
    //            Assertions.assertThat(response.getRoles()).isEqualTo(updateRequest.getRoles());
    //
    //        }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_validRequest_success() {
        // GIVEN
        String userId = user.getUserId();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        // WHEN
        userService.deleteUser(user.getUserId());
        // THEM
        verify(userRepository).deleteById(userId);
    }
}
