package com.real_estate.post.daos.impls;

import com.real_estate.post.daos.interfaces.LikeDao;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.LikeEntity;
import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import com.real_estate.post.models.postgresql.PostSellPostgresEntity;
import com.real_estate.post.models.postgresql.LikePostgresEntity;
import com.real_estate.post.repositories.LikeRepository;
import com.real_estate.post.utils.TypePost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("likeImpl")
public class LikeImpl implements LikeDao {
    @Autowired
    LikeRepository repository;

    @Autowired
    ModelMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public LikeEntity save(LikeEntity entity) {
        LikePostgresEntity postgres = mapper.map(entity, LikePostgresEntity.class);
        postgres = repository.save(postgres);
        return mapper.map(postgres, LikeEntity.class);
    }

    @Override
    public void delete(Long likeId, Long likerId) {
        repository.deleteBy(likeId, likerId);
    }

    @Override
    public Long getId(Long postId, Long finderId, TypePost typePost) {
        List<LikePostgresEntity> postgresEntities = repository.findBy(postId, finderId, typePost);
        if (postgresEntities.size() > 0) return postgresEntities.get(0).getLikeId();
        return 0L;
    }

    @Override
    public List<Object> findAllLike(Long likerId) {
        Query querySell = entityManager.createQuery(
                "select p, a , l " +
                        "from PostSellPostgresEntity p " +
                        "join LikePostgresEntity l on p.postSellId = l.postId " +
                        "join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where l.likerId = :likerId and l.typePost = :typePost " +
                        "order by l.createdAt desc"
        );
        querySell.setParameter("likerId", likerId);
        querySell.setParameter("typePost", TypePost.SELL);
        List<Object[]> sells = querySell.getResultList();

        Query queryBuy = entityManager.createQuery(
                "select p, a , l " +
                        "from PostBuyPostgresEntity p " +
                        "join LikePostgresEntity l on p.postBuyId = l.postId " +
                        "join AccountPostgresEntity a on p.authorId = a.accountId " +
                        "where l.likerId = :likerId and l.typePost = :typePost " +
                        "order by l.createdAt desc"
        );
        queryBuy.setParameter("likerId", likerId);
        queryBuy.setParameter("typePost", TypePost.BUY);
        List<Object[]> buys = queryBuy.getResultList();

        List<Object[]> combinedList = new ArrayList<>();
        combinedList.addAll(sells);
        combinedList.addAll(buys);

        // Sắp xếp danh sách kết hợp theo s.createdAt giảm dần
        combinedList.sort((o1, o2) -> {
            LikePostgresEntity s1 = (LikePostgresEntity) o1[2];
            LikePostgresEntity s2 = (LikePostgresEntity) o2[2];
            return Long.compare(s2.getCreatedAt(), s1.getCreatedAt());
        });

        List<Object> result = new ArrayList<Object>();
        for (Object[] o : combinedList) {
            Object post = o[0];
            AccountPostgresEntity account = (AccountPostgresEntity) o[1];
            LikePostgresEntity savePost = (LikePostgresEntity) o[2];

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
    public List<AccountEntity> findLiker(Long postId, TypePost typePost) {
        Query query = entityManager.createQuery(
                "select a " +
                        "from LikePostgresEntity l " +
                        "join AccountPostgresEntity a on l.likerId = a.accountId " +
                        "where l.postId = :postId and l.typePost = :typePost " +
                        "order by l.createdAt desc"
        );
        query.setParameter("postId", postId);
        query.setParameter("typePost", typePost);
        List<AccountPostgresEntity> postgresEntities = query.getResultList();
        return postgresEntities.stream().map(item -> {
            return mapper.map(item, AccountEntity.class);
        }).toList();
    }


}
