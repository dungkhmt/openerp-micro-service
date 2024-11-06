package openerp.openerpresourceserver.infrastructure.output.persistence.mapper;

import openerp.openerpresourceserver.domain.model.ExampleModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.ExampleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExamplePersistenceMapper extends IBasePersistenceMapper<ExampleModel, ExampleEntity> {
    ExamplePersistenceMapper INSTANCE = Mappers.getMapper(ExamplePersistenceMapper.class);
}
