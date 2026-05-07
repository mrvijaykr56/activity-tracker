package com.activity.tracker.mapper;

import com.activity.tracker.dto.UserDTO;
import com.activity.tracker.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO dto);

    UserDTO toDto(User entity);
}
