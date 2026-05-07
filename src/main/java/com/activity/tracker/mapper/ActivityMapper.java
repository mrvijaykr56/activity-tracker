package com.activity.tracker.mapper;

import com.activity.tracker.dto.ActivityDTO;
import com.activity.tracker.entities.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    
    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    @Mapping(target = "user", ignore = true)
    Activity toEntity(ActivityDTO dto);

    ActivityDTO toDto(Activity entity);

    List<ActivityDTO> toDtoList(List<Activity> entities);
    
    List<Activity> toEntityList(List<ActivityDTO> dtos);
}
