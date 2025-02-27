package openerp.openerpresourceserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.DeliveryTripItem;

@Repository
public interface DeliveryTripItemRepository extends JpaRepository<DeliveryTripItem, String> {
}
