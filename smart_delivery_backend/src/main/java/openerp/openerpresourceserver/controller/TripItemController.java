package openerp.openerpresourceserver.controller;


import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.TripItem;
import openerp.openerpresourceserver.service.TripItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/trip-items")
@RequiredArgsConstructor
public class TripItemController {

    private final TripItemService tripItemService;

    /**
     * Get all trip items for a specific trip
     */
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'DRIVER')")
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TripItem>> getTripItemsByTripId(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripItemService.getTripItemsByTripId(tripId));
    }
    /**
     * Get all trip items delivering for a specific trip
     */
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'DRIVER')")
    @GetMapping("/trip/{tripId}/delivering")
    public ResponseEntity<List<TripItem>> getTripItemsByTripIdDelivering(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripItemService.getTripItemsByTripIdDelivering(tripId));
    }

    /**
     * Confirm out (pickup) for multiple order items in a trip
     */
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @PutMapping("/confirm-out")
    public ResponseEntity<?> confirmOutTripItems(@RequestBody List<UUID> tripItemIds, Authentication authentication) {
        String username = authentication.getName();
        boolean success = tripItemService.confirmOutTripItems(tripItemIds, username);

        if (success) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Successfully confirmed out " + tripItemIds.size() + " items",
                    "itemCount", tripItemIds.size()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to confirm out items"
                    ));
        }
    }

    /**
     * Confirm in (delivery) for multiple order items in a trip
     */
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @PutMapping("/confirm-in")
    public ResponseEntity<?> confirmInTripItems(@RequestBody List<UUID> tripItemIds, Authentication authentication) {
        String username = authentication.getName();
        boolean success = tripItemService.confirmInTripItems(tripItemIds, username);

        if (success) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Successfully confirmed in " + tripItemIds.size() + " items",
                    "itemCount", tripItemIds.size()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to confirm in items"
                    ));
        }
    }

    /**
     * Get all trip items by their IDs
     */
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'DRIVER')")
    @GetMapping("/batch")
    public ResponseEntity<List<TripItem>> getTripItemsByIds(@RequestParam("ids") List<UUID> tripItemIds) {
        return ResponseEntity.ok(tripItemService.getTripItemsByIds(tripItemIds));
    }
}