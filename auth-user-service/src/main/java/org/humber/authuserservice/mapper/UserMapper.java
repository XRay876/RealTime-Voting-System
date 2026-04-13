package org.humber.authuserservice.mapper;

import org.humber.authuserservice.dto.response.UserResponse;
import org.humber.authuserservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);
}