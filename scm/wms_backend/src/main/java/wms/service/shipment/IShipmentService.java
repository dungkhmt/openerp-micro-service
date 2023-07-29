package wms.service.shipment;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.shipment.*;
import wms.entity.Shipment;
import wms.entity.ShipmentItem;
import wms.exception.CustomException;

public interface IShipmentService {
    Shipment createShipment(ShipmentDTO shipmentDTO, JwtAuthenticationToken token) throws CustomException;
    ShipmentItem createShipmentItem(ShipmentItemDTO shipmentItemDTO) throws CustomException;
    Shipment updateShipment(ShipmentDTO shipmentDTO, Long id) throws CustomException;
    void assignShipmentItem(AssignedItemDTO assignedItemDTO) throws CustomException;
    void unassignShipmentItem(AssignedItemDTO assignedItemDTO) throws CustomException;
    ReturnPaginationDTO<Shipment> getAllShipments(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ReturnPaginationDTO<ShipmentItem> getAllShipmentItems(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ReturnPaginationDTO<ReturnShipmentItemDTO> getAllItemOfTrip(int page, int pageSize, String sortField, boolean isSortAsc, String tripCode) throws JsonProcessingException;
    Shipment getShipmentById(long id);
    Shipment getShipmentByCode(String code);
    ShipmentItem getShipmentItemById(long id);
    ShipmentItem getShipmentItemByCode(String code);
    Shipment updateShipment(ProductDTO productDTO, long id) throws CustomException;
    void deleteShipmentById(long id);
}
