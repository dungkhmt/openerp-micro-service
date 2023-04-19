package com.hust.baseweb.repo;

import com.hust.baseweb.entity.Application;
import com.hust.baseweb.entity.ApplicationType;
import com.hust.baseweb.entity.SecurityPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, String> {

    List<Application> findByTypeAndPermissionIn(ApplicationType type, List<SecurityPermission> permissions);

    @Query(value = "select\n" +
                   "\tpermission_id\n" +
                   "from\n" +
                   "\tuser_login_security_group ulsg\n" +
                   "inner join security_group sg on\n" +
                   "\tulsg .group_id = sg.group_id\n" +
                   "inner join security_group_permission sgp on\n" +
                   "\tsg.group_id = sgp.group_id\n" +
                   "where\n" +
                   "\tulsg .user_login_id = ?1\n" +
                   "\tand permission_id like 'SCR_%'",
           nativeQuery = true)
    List<String> getScrSecurInfo(String userId);
}
