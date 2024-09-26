package com.hust.baseweb.rest.user;

import com.hust.baseweb.entity.PartyType;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(exported = true, excerptProjection = UserRestBriefProjection.class)
// @RepositoryRestResource(exported = true)
public interface UserRestRepository extends PagingAndSortingRepository<DPerson, UUID>,
    QuerydslPredicateExecutor<DPerson>, QuerydslBinderCustomizer<QDPerson> {

    Page<DPerson> findByType(PartyType type, Pageable page);

    @Query(
        "select p from DPerson p where p.type.id = :type and status.id = :status and COALESCE(concat(trim(p.person.firstName), trim(p.person.middleName), trim(p.person.lastName)),'') like %:fullNameString%")
    Page<UserRestBriefProjection> findByTypeAndStatusAndFullNameLike(
        Pageable page,
        String type,
        String status,
        String fullNameString
    );

    @Query(
        "select p from DPerson p where p.userLogin.userLoginId = :userLoginId")
    Page<UserRestBriefProjection> findByLoginUserId(
        Pageable page,
        String userLoginId
    );


    default void customize(final QuerydslBindings bindings, final QDPerson store) {
        // bindings.bind(store.address.city).single((path, value) ->
        // path.startsWith(value));
        // bindings.bind(String.class).s
        // single((StringPath path, String value) -> path.contains(value));

        bindings
            .bind(String.class)
            .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }
}
