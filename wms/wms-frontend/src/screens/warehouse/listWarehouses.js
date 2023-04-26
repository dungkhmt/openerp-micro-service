import { useRouteMatch } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { ListingMaps } from 'components/map/maps';
import MapIcon from '@mui/icons-material/Map';
import AddIcon from '@mui/icons-material/Add';
import { successNoti } from "utils/notification";
import { request } from "api";
import CommandBarButton from 'components/button/commandBarButton';
import React, { useEffect } from "react";
import { useState } from "react";
import StandardTable from 'components/table/StandardTable';
import { API_PATH } from "../apiPaths";
import { Box, Modal } from '@material-ui/core';

const ListWarehouse = () => {
  let { path } = useRouteMatch();
  const [isHideCommandBar, setHideCommandBar] = useState(true);
  const [warehousesTableData, setWarehousesTableData] = useState([]);
  const [isMapModalOpen, setMapModalOpen] = useState(false);

  useEffect(() => {
    request(
      "get",
      API_PATH.WAREHOUSE,
      (res) => {
        const tableData = res.data.map(obj => {
          obj.tableData = { "checked": false };
          return obj;
        })
        setWarehousesTableData(tableData);
      })
  }, []);

  const columns = [
    { title: "Tên", field: "name" },
    { title: "Code", field: "code" },
    { title: "Địa chỉ", field: "address" }
  ];

  const removeSelectedFacilities = () => {
    const selectedFacilityIds = warehousesTableData
      .filter((facility) => facility.tableData.checked == true)
      .map((obj) => obj.warehouseId);
    
    console.log("warehousesTableData: ", warehousesTableData);

    request(
      "delete",
      API_PATH.WAREHOUSE,
      (res) => { 
        successNoti("Xóa thành công");
        const newTableData = warehousesTableData.filter(
          (facility) => !selectedFacilityIds.includes(facility.warehouseId));
        setWarehousesTableData(newTableData);
        setHideCommandBar(true);
      },
      { },
      selectedFacilityIds
    )
  }

  const onSelectionChangeHandle = (rows) => {
    setWarehousesTableData(warehousesTableData);
    if (rows.length === 0) {
      setHideCommandBar(true);
    } else {
      setHideCommandBar(false);
    }
  }

  return <div>
    <Modal
      open={isMapModalOpen}
      onClose={() => setMapModalOpen(!isMapModalOpen)}
    >
      <Box
        sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          width: '75%',
          height: '90%',
          transform: 'translate(-50%, -50%)',
          bgcolor: 'background.paper',
          border: '2px solid #000',
          boxShadow: 24,
          p: 4,
        }}>
          <div style={{
            display: "flex",
            flexDirection: "row",
            width: "100%",
            height: "100%",
          }}>
            <ListingMaps
              warehouses={warehousesTableData
                .filter((warehouse) => warehouse.latitude != null && warehouse.longitude != null)}
            ></ListingMaps>
          </div>
      </Box>
    </Modal>
    <div>
      <StandardTable
        title={"Danh sách nhà kho"}
        columns={columns}
        data={warehousesTableData}
        hideCommandBar={isHideCommandBar}
        options={{
          selection: true,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
        onRowClick={ (event, rowData) => {
          window.location.href = `${path}/update/${rowData.warehouseId}`;
        } } 
        onSelectionChange={onSelectionChangeHandle}
        commandBarComponents={ <CommandBarButton 
                                  onClick={removeSelectedFacilities}>
                                    Xóa
                                </CommandBarButton> }
        actions={[
          {
            icon: () => <MapIcon />,
            tooltip: "Xem kho trên bản đồ",
            onClick: () => setMapModalOpen(true),
            isFreeAction: true
          },
          {
            icon: () => <Link to={`warehouse/create`}>
              <AddIcon />
            </Link>,
            tooltip: "Thêm mới kho",
            isFreeAction: true
          }
        ]}
      />
    </div>
  </div>
}

export default ListWarehouse;
