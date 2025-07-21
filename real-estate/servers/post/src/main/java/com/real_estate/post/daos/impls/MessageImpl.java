package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.MessageDao;
import com.real_estate.post.models.MessageEntity;
import com.real_estate.post.models.postgresql.MessagePostgresEntity;
import com.real_estate.post.repositories.MessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("messageImpl")
public class MessageImpl implements MessageDao {
    @Autowired
    MessageRepository repository;

    @Autowired
    ModelMapper mapper;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public MessageEntity saveMessage(MessageEntity entity) {
        MessagePostgresEntity postgres = mapper.map(entity, MessagePostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, MessageEntity.class);
    }

    @Override
    public List<MessageEntity> findLast20By(Long conversationId, Long messageId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select m " +
                   "from MessagePostgresEntity m " +
                   "where m.conversationId = :conversationId ");
        if (messageId > 0) {
            sql.append(" and m.messageId <= " + messageId + " ");
        }
        sql.append(" order by m.createdAt desc limit 20");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("conversationId", conversationId);
        List<MessagePostgresEntity> postgresEntities = query.getResultList();
        return postgresEntities.stream().map(item -> {
            return mapper.map(item, MessageEntity.class);
        }).toList();
    }
}
