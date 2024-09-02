package com.example.Interaction_service.mapper;


import com.example.Interaction_service.dto.request.CreateLikeRequest;
import com.example.Interaction_service.dto.response.LikeResponse;
import com.example.Interaction_service.dto.response.UserResponse;
import com.example.Interaction_service.entity.Like;
import org.mapstruct.Mapper;

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