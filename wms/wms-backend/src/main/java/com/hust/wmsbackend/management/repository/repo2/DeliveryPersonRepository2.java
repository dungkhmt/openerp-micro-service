package com.hust.wmsbackend.management.repository.repo2;

import com.hust.wmsbackend.management.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPersonRepository2 extends JpaRepository<DeliveryPerson, String> {

    @Query("SELECT dp.fullName FROM DeliveryPerson dp WHERE dp.userLoginId = :userLoginId")
    String findFullNameByUserLoginId(@Param("userLoginId") String userLoginId);

}
