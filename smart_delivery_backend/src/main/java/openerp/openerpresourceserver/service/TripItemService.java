package openerp.openerpresourceserver.service;


import openerp.openerpresourceserver.entity.TripItem;

import java.util.List;
import java.util.UUID;

public interface TripItemService {

    /**
     * Get all trip items for a specific trip
     * @param tripId The ID of the trip
     * @return List of trip items for the specified trip
     */
    List<TripItem> getTripItemsByTripId(UUID tripId);

    /**
     * Confirm out (pickup) for multiple order items in a trip
     * @param tripItemIds List of trip item IDs to confirm out
     * @param username Username of the staff confirming the items
     * @return true if successful, false otherwise
     */
    boolean confirmOutTripItems(List<UUID> tripItemIds, String username);

    /**
     * Confirm in (delivery) for multiple order items in a trip
     * @param tripItemIds List of trip item IDs to confirm in
     * @param username Username of the staff confirming the items
     * @return true if successful, false otherwise
     */
    boolean confirmInTripItems(List<UUID> tripItemIds, String username);

    /**
     * Get trip items by their IDs
     * @param tripItemIds List of trip item IDs to retrieve
     * @return List of trip items
     */
    List<TripItem> getTripItemsByIds(List<UUID> tripItemIds);

    List<TripItem> getTripItemsByTripIdDelivering(UUID tripId);

}