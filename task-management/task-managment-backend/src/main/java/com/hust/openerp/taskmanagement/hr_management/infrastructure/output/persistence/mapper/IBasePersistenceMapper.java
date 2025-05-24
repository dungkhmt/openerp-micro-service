package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.mapper;

public interface IBasePersistenceMapper<Model, Entity> {
    Entity toEntity(Model model);
    Model toModel(Entity entity);
}
