package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Party;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.UserLoginWithPersonModel;
import com.hust.baseweb.model.PersonModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface UserLoginRepo extends JpaRepository<UserLogin, String> {

    @Query("select new com.hust.baseweb.model.PersonModel( ul.userLoginId, p.lastName, p.middleName, p.firstName) from UserLogin ul " +
           "inner join Party pa on ul.party = pa inner join Person p on p.partyId = ul.party and  (ul.userLoginId like %:keyword% )")
    Page<PersonModel> searchUser(Pageable pageable, @Param("keyword") String keyword);

    UserLogin findByUserLoginId(String userLoginId);

    List<UserLogin> findByParty(Party party);

    @Query(value = "select ul.*\n" +
                   "from user_login ul inner join public.user_login_security_group ulsg\n" +
                   "on ul.user_login_id = ulsg.user_login_id\n" +
                   "where ulsg.group_id = ?1",
           nativeQuery = true)
    List<UserLogin> findAllUserLoginsByGroupId(String groupId);

    @Query(value = "select group_id\n" +
                   "from user_login_security_group\n" +
                   "where user_login_id = ?1",
           nativeQuery = true)
    List<String> findGroupPermsByUserLoginId(String userLoginId);

    @Query(value = "select\n" +
                   "\tur.email\n" +
                   "from\n" +
                   "\tuser_login ul\n" +
                   "inner join user_register ur on\n" +
                   "\tul.user_login_id = ur.user_login_id and ur.status_id != 'USER_DISABLED'\n",
           nativeQuery = true)
    List<String> findAllUserEmail();

    @Query(value = "select u.user_login_id from user_login u inner join user_login_security_group ug \n" +
                   " on u.user_login_id = ug.user_login_id \n" +
                   "where ug.group_id = ?1", nativeQuery = true)
    List<String> findAllUserLoginOfGroup(String groupId);

    @Query(value = "SELECT ul.* FROM user_login ul WHERE ul.party_id = :partyId", nativeQuery = true)
    UserLogin finByPartyId(@Param("partyId") UUID partyId);

    @Query(
        nativeQuery = true,
        value = "SELECT u.user_login_id FROM user_login u " +
           "WHERE UPPER(u.user_login_id) LIKE CONCAT('%', UPPER(:partOfLoginId), '%') " +
           "AND u.enabled = true " +
           "LIMIT :limit"
    )
    List<String> findByEnabledLoginIdContains(@Param("partOfLoginId") String partOfLoginId,
                                              @Param("limit") Integer limit);

    @Query(
        nativeQuery = true,
        value = "SELECT DISTINCT ul.user_login_id userLoginId, ur.email email, ur.affiliations affiliations, " +
                "   p.first_name firstName, p.middle_name middleName, p.last_name lastName, p.gender gender, p.birth_date birthDate " +
                "FROM user_login ul " +
                "INNER JOIN user_login_security_group ul_sg " +
                "   ON ul.user_login_id = ul_sg.user_login_id AND ul_sg.group_id IN :securityGroupIds " +
                "LEFT JOIN user_register ur ON ul.user_login_id = ur.user_login_id " +
                "LEFT JOIN person p ON ul.party_id = p.party_id " +
                "WHERE CONCAT(COALESCE(p.first_name, ''), ' ', COALESCE(p.middle_name, ''), ' ', COALESCE(p.last_name, '')) LIKE CONCAT('%', :search, '%') " +
                "   OR ul.user_login_id LIKE CONCAT('%', :search, '%') " +
                "   OR ur.affiliations LIKE CONCAT('%', :search, '%') " +
                "   OR ur.email LIKE CONCAT('%', :search, '%')"
    )
    Page<UserLoginWithPersonModel> findUserLoginWithPersonModelBySecurityGroupId(
        @Param("securityGroupId") Collection<String> securityGroupIds,
        @Param("search") String search,
        Pageable pageable
    );


}
