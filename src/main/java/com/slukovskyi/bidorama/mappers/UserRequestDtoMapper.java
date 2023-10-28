package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.UserRequestDto;
import com.slukovskyi.bidorama.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequestDtoMapper {

    User userRequestDtoToUser(UserRequestDto userRequestDto);
}
