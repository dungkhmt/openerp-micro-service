package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.DeliveryTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryTripRepository extends JpaRepository<DeliveryTrip, String> {

    List<DeliveryTrip> findAllByIsDeletedIsFalseOrderByCreatedStampDesc();

    List<DeliveryTrip> findAllByShipmentIdAndIsDeletedIsFalseOrderByCreatedStampDesc(String shipmentId);

    @Query(value = "select dt.delivery_trip_id , dt.vehicle_id, dt.delivery_person_id, " +
        "dt.distance, dt.total_weight, dt.total_locations, dt.last_updated_stamp, " +
        "dt.created_stamp, dt.created_by, dt.is_deleted, dt.warehouse_id, dt.shipment_id, dt.status, " +
        "dt.description " +
        "from wms_delivery_trip dt  " +
        "join wms_shipment s on dt.shipment_id = s.shipment_id " +
        "where date(now()) = date(s.expected_delivery_stamp) " +
        "and dt.delivery_person_id = ?1 " +
        "and dt.status in ?2 " +
        "and dt.is_deleted = false ", nativeQuery = true)
    List<DeliveryTrip> findTodayDeliveryTripsByPerson(String deliveryPersonId, List<String> deliveryStatus);

    List<DeliveryTrip> findAllByWarehouseId(UUID warehouseId);
}
