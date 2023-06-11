package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.Receipt;
import com.hust.wmsbackend.management.entity.enumentity.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

    List<Receipt> findAllByStatusOrderByCreatedStampDesc(ReceiptStatus status);

    List<Receipt> findAllByStatusInOrderByCreatedStampDesc(List<ReceiptStatus> statuses);

    List<Receipt> findAllByOrderByCreatedStampDesc();

}
