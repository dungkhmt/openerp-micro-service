import { useRouteMatch } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { ListingMaps } from 'components/map/maps';
import MapIcon from '@mui/icons-material/Map';
import AddIcon from '@mui/icons-material/Add';
import { errorNoti, successNoti } from "utils/notification";
import { request } from "api";
import React, { useEffect } from "react";
import { useState } from "react";
import StandardTable from 'components/StandardTable';
import { API_PATH } from "../apiPaths";
import { Box, Modal } from '@material-ui/core';
import { IconButton } from '@mui/material';
import LoadingScreen from 'components/common/loading/loading';
import withScreenSecurity from 'components/common/withScreenSecurity';

const ListWarehouse = () => {
  let { path } = useRouteMatch();
  const [isHideCommandBar, setHideCommandBar] = useState(true);
  const [warehousesTableData, setWarehousesTableData] = useState([]);
  const [isMapModalOpen, setMapModalOpen] = useState(false);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.WAREHOUSE,
        (res) => {
          const tableData = res.data.map(obj => {
            obj.tableData = { "checked": false };
            return obj;
          })
          setWarehousesTableData(tableData);
      });
      setLoading(false);
    }
    fetchData();
  }, []);

  const columns = [
    { title: "Tên", field: "name" },
    { title: "Code", field: "code" },
    { title: "Địa chỉ", field: "address" }
  ];

  const onSelectionChangeHandle = (rows) => {
    setWarehousesTableData(warehousesTableData);
    if (rows.length === 0) {
      setHideCommandBar(true);
    } else {
      setHideCommandBar(false);
    }
  }

  return (
  isLoading ? <LoadingScreen /> :
  <div>
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
        actions={[
          {
            icon: <IconButton onClick={() => setMapModalOpen(true)}>
              <MapIcon /></IconButton>,
            tooltip: "Xem kho trên bản đồ",
            isFreeAction: true
          },
          {
            icon: <Link to={`warehouse/create`}>
              <AddIcon />
            </Link>,
            tooltip: "Thêm mới kho",
            isFreeAction: true
          }
        ]}
        rowKey='warehouseId'
        editable={{
          onRowDelete: (selectedIds) => new Promise((resolve, reject) => {
            setTimeout(() => {
              request(
                "delete",
                API_PATH.WAREHOUSE,
                (res) => {
                  const deleteData = warehousesTableData.filter(
                    warehouse => !selectedIds.includes(warehouse["warehouseId"])
                  );
                  setWarehousesTableData(deleteData);
                  successNoti(`Đã xóa ${selectedIds.length} bản ghi`);
                },
                {
                  500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
                },
                selectedIds
              )
            })
          })
        }}
      />
    </div>
  </div>
  );
}

const SCR_ID = "SCR_WMSv2_WAREHOUSE_LISTING";
export default withScreenSecurity(ListWarehouse, SCR_ID, true);