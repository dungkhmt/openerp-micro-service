package com.hust.baseweb.repo;

import com.hust.baseweb.entity.StatusItem;
import com.hust.baseweb.entity.UserRegister;
import com.hust.baseweb.model.getregists.RegistsOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface UserRegisterRepo extends JpaRepository<UserRegister, String> {

    List<UserRegister> findAllByStatusItem(StatusItem statusItem);

    boolean existsByUserLoginIdOrEmail(String userLoginId, String email);

    @Query(value = "select\n" +
                   "\tuser_login_id id,\n" +
                   "\tconcat(first_name , ' ', middle_name , ' ', last_name ) fullName,\n" +
                   "\temail,\n" +
                   "\tregistered_roles roles,\n" +
                   "\tcreated_stamp createdStamp\n" +
                   "from\n" +
                   "\tuser_register ur\n" +
                   "where\n" +
                   "\tstatus_id = ?1\n" +
                   "order by\n" +
                   "\tcreated_stamp desc",
           nativeQuery = true)
    List<RegistsOM> getAllRegists(String status);
}
