package com.example.Interaction_service.mapper;

import org.mapstruct.Mapper;

import com.example.Interaction_service.dto.request.CreateLikeRequest;
import com.example.Interaction_service.dto.response.LikeResponse;
import com.example.Interaction_service.dto.response.UserResponse;
import com.example.Interaction_service.entity.Like;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    Like toLike(CreateLikeRequest request);

    //    @Mapping(source = "userId", target = "userId", qualifiedByName = "mapUserIdToUserResponseInLike")
    LikeResponse toLikeResponse(Like entity);

    //    @Named("mapUserIdToUserResponseInLike")
    default UserResponse mapUserIdToUserResponseInLike(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().userId(userId).build();
    }

    //    @IterableMapping(qualifiedByName = "mapUserIdToUserResponseInLike")
    //    Set<UserResponse> mapUserIds(Set<String> userIds);
}
