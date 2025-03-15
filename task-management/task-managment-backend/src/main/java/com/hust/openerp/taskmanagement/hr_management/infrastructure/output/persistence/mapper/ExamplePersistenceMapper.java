package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.mapper;

import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ExampleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamplePersistenceMapper extends IBasePersistenceMapper<ExampleModel, ExampleEntity> {
    ExamplePersistenceMapper INSTANCE = Mappers.getMapper(ExamplePersistenceMapper.class);
}
