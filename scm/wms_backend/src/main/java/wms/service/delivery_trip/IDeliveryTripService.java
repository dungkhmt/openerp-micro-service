package wms.service.delivery_trip;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import wms.dto.ReturnPaginationDTO;
import wms.dto.delivery_trip.DeliveryTripDTO;
import wms.dto.product.ProductDTO;
import wms.entity.DeliveryTrip;
import wms.entity.ShipmentItem;
import wms.exception.CustomException;

import java.util.List;

public interface IDeliveryTripService {
    DeliveryTrip createDeliveryTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<DeliveryTrip> getAllDeliveryTrips(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    List<DeliveryTrip> getTripToAssignBill(String billCode) throws JsonProcessingException;
    DeliveryTrip getDeliveryTripById(long id);
    DeliveryTrip getDeliveryTripByCode(String code);
    DeliveryTrip updateDeliveryTrip(ProductDTO productDTO, long id) throws CustomException;
    void deleteDeliveryTripById(long id);
    ShipmentItem assignBillToTrip(DeliveryTripDTO deliveryTripDTO, JwtAuthenticationToken token) throws CustomException;
    void createTripRoute(String tripCode) throws CustomException;
}
