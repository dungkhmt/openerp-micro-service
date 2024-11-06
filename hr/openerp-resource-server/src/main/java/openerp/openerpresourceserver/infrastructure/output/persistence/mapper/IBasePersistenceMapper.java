package openerp.openerpresourceserver.infrastructure.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public interface IBasePersistenceMapper<Model, Entity> {
    Entity toEntity(Model model);
    Model toModel(Entity entity);
}
