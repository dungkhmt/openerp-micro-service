package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.ConversationDao;
import com.real_estate.post.models.ConversationEntity;
import com.real_estate.post.models.postgresql.ConversationPostgresEntity;
import com.real_estate.post.repositories.ConversationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("conversationImpl")
public class ConversationImpl implements ConversationDao {
    @Autowired
    ConversationRepository repository;

    @Autowired
    ModelMapper mapper;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public ConversationEntity saveConversation(ConversationEntity entity) {
        ConversationPostgresEntity postgres = mapper.map(entity, ConversationPostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, ConversationEntity.class);
    }

    @Override
    public Optional<ConversationEntity> findById(Long conversationId) {
        Optional<ConversationPostgresEntity> postgresEntityOptional = repository.findById(conversationId);
        return postgresEntityOptional.map(postgre -> mapper.map(postgre, ConversationEntity.class));
    }

    @Override
    public List<ConversationEntity> findByMember(Long id, Long otherId) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select c " +
                "from ConversationPostgresEntity c " +
                "where " + id + " member of c.memberIds "
        );
        if (otherId > 0) {
            sql.append("and " + otherId + " member of c.memberIds");
        }
        Query query = entityManager.createQuery(sql.toString());
        List<ConversationPostgresEntity> postgresEntities = query.getResultList();
        List<ConversationEntity> entities = new ArrayList<ConversationEntity>();
        for (ConversationPostgresEntity postgres : postgresEntities) {
            entities.add(mapper.map(postgres, ConversationEntity.class));
        }
        return entities;
    }
}
