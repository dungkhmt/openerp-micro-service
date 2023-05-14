package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.DeliveryTripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryTripItemRepository extends JpaRepository<DeliveryTripItem, String> {

    List<DeliveryTripItem> findAllByDeliveryTripIdAndIsDeletedIsFalse(String deliveryTripId);

    List<DeliveryTripItem> findAllByDeliveryTripIdAndIsDeleted(String deliveryTripId, boolean isDeleted);

    Optional<DeliveryTripItem> findByDeliveryTripItemIdAndIsDeletedIsFalse(String id);

    @Modifying(clearAutomatically = true)
    @Query("update DeliveryTripItem item set item.sequence = ?1 where item.deliveryTripItemId = ?2")
    @Transactional
    void updateSequenceByDeliveryItemId(int sequence, String itemId);

    @Query(value = "select sum(dti.quantity) from delivery_trip_item dti " +
           "join assigned_order_item aoi on aoi.assigned_order_item_id = dti.assigned_order_item_id " +
           "join product p on p.product_id = aoi.product_id " +
           "where dti.order_id = ?1 and dti.is_deleted = false and (dti.status = 'DONE' or dti.status = 'FAIL') " +
           "and p.product_id = ?2 ", nativeQuery = true)
    Long getTotalCompleteDeliveryItemByOrderIdAndProductId(UUID orderId, UUID productId);

    @Query(value = "select sum(dti.quantity) from delivery_trip_item dti " +
                   "join assigned_order_item aoi on aoi.assigned_order_item_id = dti.assigned_order_item_id " +
                   "join product p on p.product_id = aoi.product_id " +
                   "where dti.order_id = ?1 and dti.is_deleted = false and dti.status = 'DONE' " +
                   "and p.product_id = ?2 ", nativeQuery = true)
    Long getTotalDoneDeliveryItemByOrderIdAndProductId(UUID orderId, UUID productId);
}
