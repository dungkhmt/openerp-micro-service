package wms.service.delivery_trip;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import wms.dto.ReturnPaginationDTO;
import wms.dto.delivery_trip.DeliveryTripDTO;
import wms.dto.delivery_trip.TripRouteDTO;
import wms.dto.product.ProductDTO;
import wms.dto.vehicle.DroneDTO;
import wms.entity.DeliveryTrip;
import wms.entity.DroneEntity;
import wms.entity.RouteSchedulingOutput;
import wms.entity.ShipmentItem;
import wms.exception.CustomException;

import java.util.List;

public interface IDeliveryTripService {
    DeliveryTrip createDeliveryTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<DeliveryTrip> getAllDeliveryTrips(int page, int pageSize, String sortField, boolean isSortAsc, String shipmentCode) throws JsonProcessingException;
    DeliveryTrip updateDeliveryTrip(DeliveryTripDTO deliveryTripDTO, long id) throws CustomException;
    void deleteDeliveryTrip(long id);
    List<DeliveryTrip> getTripToAssignBill(String billCode) throws JsonProcessingException;
    DeliveryTrip getDeliveryTripById(long id);
    DeliveryTrip getDeliveryTripByCode(String code);
    void deleteDeliveryTripById(long id);
    ShipmentItem assignBillToTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException;
    void createTripRoute(TripRouteDTO tripRouteDTO) throws Exception;
    RouteSchedulingOutput getTripRoute(String tripCode);
    void deleteTripRoute(String tripCode);
}
