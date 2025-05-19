package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.VehicleType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripOrderSummaryDto {


        public TripOrderSummaryDto(Order order, Trip trip, Vehicle vehicle) {
            this.id = order.getId();
            this.senderId = order.getSenderId();
            this.senderName = order.getSenderName();
            this.recipientId = order.getRecipientId();
            this.recipientName = order.getRecipientName();
            this.orderType = order.getOrderType();
            this.status = String.valueOf(order.getStatus());
            this.totalPrice = order.getTotalPrice();
            this.shippingPrice = order.getShippingPrice();
            this.finalPrice = order.getFinalPrice();
            this.createdAt = order.getCreatedAt();
            this.tripId = trip.getId();
            this.tripCode = trip.getTripCode();
            this.date = trip.getDate();
            this.vehicleType = vehicle.getVehicleType();
            this.vehiclePlateNumber = vehicle.getPlateNumber();
        }

        private UUID id;
        private UUID senderId;
        private UUID recipientId;
        private String senderName;
        private String recipientName;
        private String orderType;
        private String status;
        private Double totalPrice;
        private Double shippingPrice;
        private Double finalPrice;
        private Timestamp createdAt;
        private UUID tripId;
        private String tripCode;
        private LocalDate date;
        private VehicleType vehicleType;
        private String vehiclePlateNumber;

}
