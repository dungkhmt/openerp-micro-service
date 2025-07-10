package openerp.openerpresourceserver.repository.impl;

import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.OrderSummaryMiddleMileDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.enumentity.RouteDirection;
import openerp.openerpresourceserver.repository.OrderRepositoryCustom;
import openerp.openerpresourceserver.utils.SqlQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @Autowired
    private SqlQueryUtil sqlQueryUtil;

    @Override
    public List<OrderSummaryDTO> findOrdersCreatedToday(UUID hubId) {
        StringBuilder SQL = new StringBuilder();
        SQL.append("SELECT * FROM smartdelivery_order ");
        SQL.append("WHERE (status = 'PENDING' OR status = 'COLLECT_FAILED') ");
        SQL.append("AND origin_hub_id = :hubId");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hubId", hubId);

        return sqlQueryUtil.queryForList(SQL.toString(), params, OrderSummaryDTO.class);
    }


    @Override
    public OrderResponseDto findOrderDetailById(UUID id) {
        StringBuilder SQL1 = new StringBuilder();
        SQL1.append("SELECT O.*, ");
        SQL1.append("S.SENDER_ID, S.NAME AS SENDER_NAME, S.PHONE AS SENDER_PHONE, S.EMAIL AS SENDER_EMAIL, S.ADDRESS AS SENDER_ADDRESS, S.LONGITUDE AS SENDER_LONGITUDE, S.LATITUDE AS SENDER_LATITUDE, ");
        SQL1.append("R.RECIPIENT_ID, R.NAME AS RECIPIENT_NAME, R.PHONE AS RECIPIENT_PHONE, R.EMAIL AS RECIPIENT_EMAIL, R.ADDRESS AS RECIPIENT_ADDRESS, R.LONGITUDE AS RECIPIENT_LONGITUDE, R.LATITUDE AS RECIPIENT_LATITUDE ");
        SQL1.append("FROM smartdelivery_order o ");
        SQL1.append("JOIN smartdelivery_sender S ON O.SENDER_ID = S.SENDER_ID ");
        SQL1.append("JOIN smartdelivery_recipient R ON O.RECIPIENT_ID = R.RECIPIENT_ID ");
        SQL1.append("where o.id = :id ");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        OrderResponseDto orderResponseDto = sqlQueryUtil.queryForObject(SQL1.toString(), params, OrderResponseDto.class)
                .orElseThrow(() -> new NotFoundException("not found order"));


        StringBuilder SQL2 = new StringBuilder();
        SQL2.append("SELECT * FROM smartdelivery_order_item ");
        SQL2.append("WHERE ORDER_ID = :id ");

        List<OrderItem> orderItems = sqlQueryUtil.queryForList(SQL2.toString(), params, OrderItem.class);

        orderResponseDto.setItems(orderItems);
        return orderResponseDto;


    }

    @Override
    public List<OrderSummaryDTO> getCollectedColelctorList(UUID hubId) {
        StringBuilder SQL = new StringBuilder();
        SQL.append("SELECT * FROM smartdelivery_order ");
        SQL.append("WHERE status = 'COLLECTED_COLLECTOR' ");
        SQL.append("AND origin_hub_id = :hubId"); // Thêm điều kiện lọc theo hubId

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hubId", hubId);

        return sqlQueryUtil.queryForList(SQL.toString(), params, OrderSummaryDTO.class);
    }

    @Override
    public List<OrderSummaryDTO> getCollectedHubList(UUID hubId) {
        StringBuilder SQL = new StringBuilder();
        SQL.append("SELECT * FROM smartdelivery_order ");
        SQL.append("WHERE status = 'COLLECTED_HUB' ");
        SQL.append("AND origin_hub_id = :hubId"); // Thêm điều kiện lọc theo hubId

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hubId", hubId);

        return sqlQueryUtil.queryForList(SQL.toString(), params, OrderSummaryDTO.class);
    }

    // Search for available order for vehicle
    @Override
    public List<OrderSummaryMiddleMileDto> getCollectedCollectorListVehicle(UUID vehicleId, UUID hubId) {
        StringBuilder SQL1 = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vehicleId", vehicleId);
        params.addValue("hubId", hubId);

        //Lấy route của vehicle, sau đó lấy các đơn hàng của hub có final hub nằm trong route và final hub.sequence> origin hub.sequence
        SQL1.append("SELECT O.*, H_FINAL.code as hub_code, H_FINAL.name as hub_name FROM smartdelivery_vehicle V ");
        SQL1.append("JOIN smartdelivery_route_vehicle RV ON V.vehicle_id = RV.vehicle_id ");
        SQL1.append("JOIN smartdelivery_route_stop RS_CURRENT ON RV.route_id = RS_CURRENT.route_id AND RS_CURRENT.hub_id = :hubId ");
        SQL1.append("JOIN smartdelivery_route_stop RS_FINAL ON RV.route_id = RS_FINAL.route_id ");
        SQL1.append("JOIN smartdelivery_order O ON O.final_hub_id = RS_FINAL.hub_id ");
        SQL1.append("JOIN smartdelivery_hub H_FINAL ON O.final_hub_id = H_FINAL.hub_id ");
        SQL1.append("WHERE V.vehicle_id = :vehicleId ");
        SQL1.append("AND O.final_hub_id = RS_FINAL.hub_id ");
        SQL1.append("AND RS_CURRENT.stop_sequence < RS_FINAL.stop_sequence ");
        return sqlQueryUtil.queryForList(SQL1.toString(), params, OrderSummaryMiddleMileDto.class);
    }

    @Override
    public List<Order> findAllByRouteVehicleId(UUID routeVehicleId){
        StringBuilder SQL1 = new StringBuilder();
        SQL1.append("SELECT O.*, ");
        SQL1.append("S.SENDER_ID, S.NAME AS SENDER_NAME, S.PHONE AS SENDER_PHONE, S.EMAIL AS SENDER_EMAIL, S.ADDRESS AS SENDER_ADDRESS, S.LONGITUDE AS SENDER_LONGITUDE, S.LATITUDE AS SENDER_LATITUDE, ");
        SQL1.append("R.RECIPIENT_ID, R.NAME AS RECIPIENT_NAME, R.PHONE AS RECIPIENT_PHONE, R.EMAIL AS RECIPIENT_EMAIL, R.ADDRESS AS RECIPIENT_ADDRESS, R.LONGITUDE AS RECIPIENT_LONGITUDE, R.LATITUDE AS RECIPIENT_LATITUDE ");
        SQL1.append("FROM smartdelivery_order o ");
        SQL1.append("JOIN smartdelivery_sender S ON O.SENDER_ID = S.SENDER_ID ");
        SQL1.append("JOIN smartdelivery_recipient R ON O.RECIPIENT_ID = R.RECIPIENT_ID ");
        SQL1.append("where o.route_vehicle_id = :routeVehicleId ");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("routeVehicleId", routeVehicleId);

        List<Order> orders = sqlQueryUtil.queryForList(SQL1.toString(), params, Order.class);

        return orders;

    };

    public List<OrderItem> findAllByOrderId(UUID orderId){
        StringBuilder SQL1 = new StringBuilder();
        SQL1.append("SELECT * FROM smartdelivery_order_item ");
        SQL1.append("WHERE ORDER_ID = :id ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", orderId);
        return sqlQueryUtil.queryForList(SQL1.toString(), params, OrderItem.class);
    }



}
