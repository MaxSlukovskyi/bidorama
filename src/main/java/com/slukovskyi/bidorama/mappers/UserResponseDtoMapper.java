package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.UserResponseDto;
import com.slukovskyi.bidorama.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseDtoMapper {

    UserResponseDto userToUserResponseDto(User user);

    User userResponseDtoToUser(UserResponseDto userResponseDto);
}
