package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.SavePostDao;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.SavePostEntity;
import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.models.postgresql.SavePostPostgresEntity;
import com.real_estate.post.repositories.SavePostRepository;
import com.real_estate.post.utils.TypePost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component("savePostImpl")
public class SavePostImpl implements SavePostDao {
    @Autowired
    SavePostRepository repository;

    @Autowired
    ModelMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SavePostEntity save(SavePostEntity entity) {
        SavePostPostgresEntity postgres = mapper.map(entity, SavePostPostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, SavePostEntity.class);
    }

    @Override
    public void delete(Long saveId, Long accountId) {
        repository.deleteBy(saveId, accountId);
    }

    @Override
    public Long getId(Long postId, Long accountId, TypePost typePost) {
        List<SavePostPostgresEntity> postgresEntities = repository.findBy(postId, accountId, typePost);
        if (postgresEntities.size() > 0) return postgresEntities.get(0).getSaveId();
        return 0L;
    }

    @Override
    public List<Object> findPostBySaver(Long saverId) {
        Query querySell = entityManager.createQuery(
                "select p, a , s " +
                        "from PostSellPostgresEntity p " +
                        "join SavePostPostgresEntity s on p.postSellId = s.postId " +
                        "join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where s.accountId = :accountId and s.typePost = :typePost " +
                        "order by s.createdAt desc"
        );
        querySell.setParameter("accountId", saverId);
        querySell.setParameter("typePost", TypePost.SELL);
        List<Object[]> sells = querySell.getResultList();

        Query queryBuy = entityManager.createQuery(
                "select p, a , s " +
                        "from PostBuyPostgresEntity p " +
                        "join SavePostPostgresEntity s on p.postBuyId = s.postId " +
                        "join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where s.accountId = :accountId and s.typePost = :typePost " +
                        "order by s.createdAt desc"
        );
        queryBuy.setParameter("accountId", saverId);
        queryBuy.setParameter("typePost", TypePost.BUY);
        List<Object[]> buys = queryBuy.getResultList();

        List<Object[]> combinedList = new ArrayList<>();
        combinedList.addAll(sells);
        combinedList.addAll(buys);

        // Sắp xếp danh sách kết hợp theo s.createdAt giảm dần
        combinedList.sort((o1, o2) -> {
            SavePostPostgresEntity s1 = (SavePostPostgresEntity) o1[2];
            SavePostPostgresEntity s2 = (SavePostPostgresEntity) o2[2];
            return Long.compare(s2.getCreatedAt(), s1.getCreatedAt());
        });

        List<Object> result = new ArrayList<Object>();
        for (Object[] o : combinedList) {
            Object post = o[0];
            AccountPostgresEntity account = (AccountPostgresEntity) o[1];
            SavePostPostgresEntity savePost = (SavePostPostgresEntity) o[2];

            if (post instanceof PostBuyPostgresEntity) {
                PostBuyPostgresEntity postBuy = (PostBuyPostgresEntity) post;
                result.add(new PostBuyResponseDto(postBuy, account, savePost));
            } else if (post instanceof PostSellPostgresEntity) {
                PostSellPostgresEntity postSell = (PostSellPostgresEntity) post;
                result.add(new PostSellResponseDto(postSell, account, savePost));
            } else {
                System.out.println("Unknown entity type");
            }
        }

        return result;
    }

    @Override
    public List<AccountEntity> findSaver(Long postId, TypePost typePost) {
        Query query = entityManager.createQuery(
                "select a " +
                        "from SavePostPostgresEntity s " +
                        "join AccountPostgresEntity a on s.accountId = a.accountId " +
                        "where s.postId = :postId and s.typePost = :typePost " +
                        "order by s.createdAt desc"
        );
        query.setParameter("postId", postId);
        query.setParameter("typePost", typePost);
        List<AccountPostgresEntity> postgresEntities = query.getResultList();
        return postgresEntities.stream().map(item -> {
            return mapper.map(item, AccountEntity.class);
        }).toList();
    }


}
