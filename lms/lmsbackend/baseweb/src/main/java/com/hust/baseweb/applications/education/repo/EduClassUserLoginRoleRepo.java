package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduClassUserLoginRole;
import com.hust.baseweb.applications.education.entity.compositeid.EduClassUserLoginRoleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface EduClassUserLoginRoleRepo extends JpaRepository<EduClassUserLoginRole, EduClassUserLoginRoleId> {
    EduClassUserLoginRole save(EduClassUserLoginRole eduClassUserLoginRole);

    List<EduClassUserLoginRole> findAllByUserLoginIdAndThruDate(String userLoginId, Date thruDate);

    List<EduClassUserLoginRole> findAllByClassId(UUID classId);

    @Modifying
    @Transactional
    void deleteByClassIdAndUserLoginIdAndRoleId(UUID classId, String userLoginId, String roleId);

    //Page<EduClassUserLoginRole> findAllByUserLoginIdAndThruDate(String userLoginId, Date thruDate, Pageable pageable);

    //@Query("select count(*) from edu_class_user_login_role where user_login_id = ?1 and thru_date = ?2")
    //int totalCountByUserLoginIdAndThruDate(String userLoginId, Date thruDate);
}
