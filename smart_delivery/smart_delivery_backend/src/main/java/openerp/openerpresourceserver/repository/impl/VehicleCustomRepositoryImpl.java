package openerp.openerpresourceserver.repository.impl;

import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.repository.VehicleCustomRepository;
import openerp.openerpresourceserver.utils.SqlQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class VehicleCustomRepositoryImpl implements VehicleCustomRepository {

    @Autowired
    private SqlQueryUtil sqlQueryUtil;

    @Override
    public List<VehicleDto> getVehicleByHubId(UUID hubId){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT V.VEHICLE_ID, CAST(V.status AS VARCHAR) as status, V.PLATE_NUMBER, V.MODEL, V.MANUFACTURER, V.DRIVER_NAME ");
        sql.append("FROM SMARTDELIVERY_VEHICLE V ");
        sql.append("JOIN SMARTDELIVERY_ROUTE_VEHICLE RV ON V.VEHICLE_ID = RV.VEHICLE_ID ");
        sql.append("JOIN SMARTDELIVERY_ROUTE R ON RV.ROUTE_ID = R.ROUTE_ID ");
        sql.append("JOIN SMARTDELIVERY_ROUTE_STOP S ON S.ROUTE_ID = R.ROUTE_ID ");
        sql.append("WHERE S.HUB_ID = :hubId");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hubId", hubId);

        return sqlQueryUtil.queryForList(sql.toString(), params, VehicleDto.class);

    }
}
