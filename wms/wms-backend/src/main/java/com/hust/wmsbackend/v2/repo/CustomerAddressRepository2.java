package com.hust.wmsbackend.v2.repo;

import com.hust.wmsbackend.management.entity.CustomerAddress;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAddressRepository2 extends JpaRepository<CustomerAddress, UUID> {
    boolean existsByUserLoginIdAndAddressNameAndLongitudeAndLatitude(String userLoginId, String addressName, BigDecimal longitude, BigDecimal latitude);

    Optional<CustomerAddress> findByUserLoginIdAndAddressNameAndLongitudeAndLatitude(
            String userLoginId, String addressName, BigDecimal longitude, BigDecimal latitude);
}
