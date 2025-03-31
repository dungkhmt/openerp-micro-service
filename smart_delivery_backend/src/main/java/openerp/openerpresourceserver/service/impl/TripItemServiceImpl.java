package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.TripItem;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import openerp.openerpresourceserver.repository.OrderItemRepo;
import openerp.openerpresourceserver.repository.TripItemRepository;
import openerp.openerpresourceserver.repository.TripRepository;
import openerp.openerpresourceserver.service.TripItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripItemServiceImpl implements TripItemService {

    private final TripItemRepository tripItemRepository;
    private final OrderItemRepo orderItemRepo;
    private final TripRepository tripRepository;

    @Override
    public List<TripItem> getTripItemsByTripId(UUID tripId) {
        // Check if trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        return tripItemRepository.findAllByTripId(tripId);
    }

    @Override
    @Transactional
    public boolean confirmOutTripItems(List<UUID> tripItemIds, String username) {
        try {
            List<TripItem> tripItems = tripItemRepository.findAllById(tripItemIds);

            // Validate that all trip items exist
            if (tripItems.size() != tripItemIds.size()) {
                log.error("Some trip items were not found");
                return false;
            }

            List<OrderItem> orderItemsToUpdate = new ArrayList<>();

            for (TripItem tripItem : tripItems) {
                // Update trip item status and confirmation details
                tripItem.setStatus("CONFIRMED_OUT");
                tripItem.setConfirmedOutBy(username);

                // Get the associated order item
                OrderItem orderItem = orderItemRepo.findById(tripItem.getOrderItemId())
                        .orElseThrow(() -> new NotFoundException("Order item not found with ID: " + tripItem.getOrderItemId()));

                // Update order item status
                orderItem.setStatus(OrderItemStatus.CONFIRMED_OUT);
                orderItemsToUpdate.add(orderItem);
            }

            // Save all updates
            tripItemRepository.saveAll(tripItems);
            orderItemRepo.saveAll(orderItemsToUpdate);

            log.info("Successfully confirmed out {} trip items by user {}", tripItemIds.size(), username);
            return true;
        } catch (Exception e) {
            log.error("Error confirming out trip items", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean confirmInTripItems(List<UUID> tripItemIds, String username) {
        try {
            List<TripItem> tripItems = tripItemRepository.findAllById(tripItemIds);
            Trip trip = tripRepository.findById(tripItems.get(0).getTripId())
                    .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripItems.get(0).getTripId()));
            trip.setStatus("CONFIRMED_IN");
            // Validate that all trip items exist
            if (tripItems.size() != tripItemIds.size()) {
                log.error("Some trip items were not found");
                return false;
            }

            List<OrderItem> orderItemsToUpdate = new ArrayList<>();

            for (TripItem tripItem : tripItems) {
                // Update trip item status and confirmation details
                tripItem.setStatus("CONFIRMED_IN");
                tripItem.setDelivered(true);
                tripItem.setConfirmedInBy(username);

                // Get the associated order item
                OrderItem orderItem = orderItemRepo.findById(tripItem.getOrderItemId())
                        .orElseThrow(() -> new NotFoundException("Order item not found with ID: " + tripItem.getOrderItemId()));

                // Update order item status
                orderItem.setStatus(OrderItemStatus.CONFIRMED_IN);
                orderItemsToUpdate.add(orderItem);
            }

            // Save all updates
            tripItemRepository.saveAll(tripItems);
            orderItemRepo.saveAll(orderItemsToUpdate);
            tripRepository.save(trip);
            log.info("Successfully confirmed in {} trip items by user {}", tripItemIds.size(), username);
            return true;
        } catch (Exception e) {
            log.error("Error confirming in trip items", e);
            return false;
        }
    }

    @Override
    public List<TripItem> getTripItemsByIds(List<UUID> tripItemIds) {
        return tripItemRepository.findAllById(tripItemIds);
    }
    @Override
    public List<TripItem> getTripItemsByTripIdDelivering(UUID tripId) {
        List<TripItem> tripItems =  tripItemRepository.findAllByTripIdAndStatus(tripId, "DELIVERING");
        tripItems.addAll(tripItemRepository.findAllByTripIdAndStatus(tripId, "DELIVERED"));
        return tripItems;
    }
}