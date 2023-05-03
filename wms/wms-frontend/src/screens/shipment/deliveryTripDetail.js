import AddIcon from '@mui/icons-material/Add';
import { Box, Button, Grid, MenuItem, Modal, Select, TextField, Typography } from "@mui/material";
import { isEditableInput } from '@testing-library/user-event/dist/utils';
import { request } from "api";
import { RouteMap } from 'components/map/maps';
import StandardTable from "components/table/StandardTable";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { errorNoti, successNoti } from 'utils/notification';

const DeliveryTripDetail = ( props ) => {
  const tripId = props.match?.params?.id;
  const classes = useStyles();

  const [tripInfo, setTripInfo] = useState({});
  const [isDeleted, setDeleted] = useState(false);
  const [deliveryPersons, setDeliveryPersons] = useState([]);
  const [warehouseList, setWarehouseList] = useState([]);
  const [deliveryItemsTableData, setDeliveryItemsTableData] = useState([]);

  const [selectedDeliveryPersonId, setSelectedDeliveryPersonId] = useState(null);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);
  const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);

  const [isShowAssignedItemsModal, setShowAssignedItemsModal] = useState(false);
  const [selectedAssignedItem, setSelectedAssignItem] = useState({});
  const [isShowQuantityModal, setShowQuantityModal] = useState(false);
  const [maxQuantity, setMaxQuantity] = useState(0);
  const [selectedQuantity, setSelectedQuantity] = useState(0);
  const [createdItemsTableData, setCreatedItemsTableData] = useState([]);
  const [rawCreatedItemsTableData, setRawCreatedItemsTableData] = useState([]);
  const [maxSequence, setMaxSequence] = useState(0);

  const [routeDetails, setRouteDetails] = useState({});
  const [isShowRouteMapModal, setShowRouteMapModal] = useState(false);
  const [runnedAutoRoute, setRunnedAutoRoute] = useState(false);

  // chọn danh sách sản phẩm (phải cùng một warehouse)
  // nếu danh sách sản phẩm của delivery trip khác rỗng -> không thể update warehouse

  useEffect(() => {
    request(
      "get",
      `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${tripId}`,
      (res) => {
        setTripInfo(res.data);
        setMaxSequence(res.data.totalLocations);
        setSelectedWarehouseId(res.data.warehouseId);
        setDeliveryItemsTableData(res.data.items);
        if (res.data.warehouseName != null) {
          setSelectedWarehouseName(res.data.warehouseName);
        }
      }
    );

    request(
      "get",
      API_PATH.DELIVERY_MANAGER_DELIVERY_PERSON,
      (res) => {
        setDeliveryPersons(res.data);
      }
    );

    request(
      "get",
      API_PATH.WAREHOUSE,
      (res) => {
        setWarehouseList(res.data);
      }
    );

    request(
      "get",
      API_PATH.DELIVERY_MANAGER_ASSIGN_ORDER_ITEM,
      (res) => {
        setRawCreatedItemsTableData(res.data);
        setCreatedItemsTableData(res.data);
      }
    );

    request(
      "get",
      `${API_PATH.DELIVREY_MANAGER_AUTO_ROUTE}/${tripId}`,
      (res) => {
        setRouteDetails(res.data);
        if (res.data.points.length > 0) {
          setRunnedAutoRoute(true);
        }
      }
    )
  }, []);

  // useEffect(() => {
  //   const assignedItemsIdArr = deliveryItemsTableData.map(e => e.assignOrderItemId);
  //   const newCreatedItemsTableData = createdItemsTableData.filter(item => !assignedItemsIdArr.includes(item.assignOrderItemId));
  //   setCreatedItemsTableData(newCreatedItemsTableData);
  // }, [deliveryItemsTableData]);

  useEffect(() => {
    // lọc created items table data 
    // loại bỏ các items có assignOrderItemId nằm trong deliveryItemsTableData
    // và loại bỏ các items có warehouseId != deliveryTrip warehouseId
    if (selectedWarehouseId != null) {
      const assignedItemsIdArr = deliveryItemsTableData.map(e => e.assignOrderItemId);
      const filterTableData = createdItemsTableData.filter(
        item => item.warehouseId == selectedWarehouseId && !assignedItemsIdArr.includes(item.assignOrderItemId));
      setCreatedItemsTableData(filterTableData);
    }
  }, [selectedWarehouseId]);

  const saveQuantityButtonHandle = () => {
    setShowQuantityModal(false);
    setShowAssignedItemsModal(false);
    const newDeliveryItem = {
      ...selectedAssignedItem,
      quantity: selectedQuantity,
      sequence: maxSequence + 1
    };
    var updated = false;
    // for (var i = 0; i < deliveryItemsTableData.length; i++) {
    //   if (deliveryItemsTableData[i].assignOrderItemId 
    //     == newDeliveryItem.assignOrderItemId) {
    //       var localDeliveryItem = deliveryItemsTableData[i];
    //       const newQuantity = parseInt(localDeliveryItem.quantity) + parseInt(newDeliveryItem.quantity);
    //       localDeliveryItem.quantity = newQuantity;
    //       var newDeliveryItemsTableData = deliveryItemsTableData;
    //       newDeliveryItemsTableData.splice(i, 1);
    //       setDeliveryItemsTableData([...newDeliveryItemsTableData, localDeliveryItem]);
    //       updated = true;
    //       break;
    //     }
    // }
    if (!updated) {
      setDeliveryItemsTableData([...deliveryItemsTableData, newDeliveryItem]);
      setMaxSequence(maxSequence + 1);
    }
    setSelectedWarehouseId(selectedAssignedItem.warehouseId);
    setSelectedWarehouseName(selectedAssignedItem.warehouseName);
    setSelectedQuantity(0);

    // remove các createdOrderItem có assignOrderItem trùng với newDeliveryItem
    var newCreatedItemsTableData = createdItemsTableData;
    var removeItemIndex;
    for (var i = 0; i < newCreatedItemsTableData.length; i++) {
      if (newCreatedItemsTableData[i].assignOrderItemId == newDeliveryItem.assignOrderItemId) {
        removeItemIndex = i;
        break;
      }
    }
    newCreatedItemsTableData.splice(removeItemIndex, 1);
    setCreatedItemsTableData(newCreatedItemsTableData);
  }

  const saveDeliveryTripButtonHandle = () => {
    request(
      "put",
      API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
      (res) => {
        if (res.status == 200) {
          setTripInfo(res.data);
          successNoti("Lưu thông tin chuyến giao hàng thành công");
        }
      },
      {
        500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
      },
      {
        ...tripInfo,
        deliveryPersonId: selectedDeliveryPersonId,
        warehouseId: selectedWarehouseId,
        totalLocations: maxSequence,
        items: [
          ...deliveryItemsTableData
        ]
      }
    )
  }

  const deleteButtonHandle = () => {
    request(
      "delete",
      `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${tripId}`,
      (res) => {
        if (res.status == 200) {
          successNoti("Hủy bỏ chuyến giao hàng thành công");
          setDeleted(true);
        }
      }
    )
  }

  const autoRouteButtonHandle = () => {
    request(
      "put",
      API_PATH.DELIVREY_MANAGER_AUTO_ROUTE,
      (res) => {
        successNoti("Đang tiến hành tìm quãng đường tối ưu");
      },
      {
        500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
      },
      {
        deliveryTripId: tripId,
        items: [
          ...deliveryItemsTableData
        ]
      }
    )
  }

  return (<Fragment>

    <Modal open={isShowAssignedItemsModal}
      onClose={() => setShowAssignedItemsModal(!isShowAssignedItemsModal)}
      aria-labelledby="modal-modal-title" 
      aria-describedby="modal-modal-description"
    >
      <Box sx={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        width: '75%',
        height: '70%',
        transform: 'translate(-50%, -50%)',
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
      }}>
        <StandardTable 
          title='Thêm sản phẩm vào chuyến giao hàng'
          hideCommandBar={true}
          columns={[
            { title: "Sản phẩm", field: "productName" },
            { title: "Số lượng", field: "quantity" },
            { title: "Kệ hàng", field: "bayCode" },
            { title: "Lô", field: "lotId" },
            { title: "Kho", field: "warehouseName" },
            { title: "Địa chỉ nhận hàng", field: "customerAddressName" }
          ]}
          options={{
            selection: false,
            pageSize: 5,
            search: true,
            sorting: true,
          }}
          data={createdItemsTableData}
          onRowClick={(event, rowData) => {
            setMaxQuantity(rowData?.quantity)
            setShowQuantityModal(true);
            setSelectedAssignItem(rowData);
          }}
        />
      </Box>
    </Modal>

    <Modal open={isShowRouteMapModal}
      onClose={() => setShowRouteMapModal(!isShowRouteMapModal)}
      aria-labelledby="modal-modal-title" 
      aria-describedby="modal-modal-description"
    >
      <Box sx={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        width: '75%',
        height: '70%',
        transform: 'translate(-50%, -50%)',
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
      }}>
        <RouteMap 
          points={routeDetails?.points}
          customers={routeDetails?.customers}
          warehouse={routeDetails?.warehouse}
        />
      </Box>
    </Modal>

    <Modal
      open={isShowQuantityModal}
      onClose={() => setShowQuantityModal(!isShowQuantityModal)}
      aria-labelledby="modal-modal-title" 
      aria-describedby="modal-modal-description">
        <Box sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          width: '25%',
          height: '5%',
          transform: 'translate(-50%, -50%)',
          bgcolor: 'background.paper',
          border: '2px solid #000',
          boxShadow: 24,
          p: 4,
        }}>
          <Typography variant="h5">
            Số lượng sản phẩm muốn thêm
          </Typography>
          <TextField 
            value={selectedQuantity}
            onChange={(e) => setSelectedQuantity(e.target.value)}
            type="number"
            InputLabelProps={{
              shrink: true,
            }}
            InputProps={{
              inputProps: { 
                  max: maxQuantity, min: 1
              }
            }}
          />
          <Button variant="contained" className={classes.addButton} 
            type="submit" onClick={saveQuantityButtonHandle}>Lưu</Button>
        </Box>
    </Modal>

    <Box>
      <Grid container justifyContent="space-between" 
        className={classes.headerBox} >
        <Grid>
          <Typography variant="h5">
            Thông tin chuyến giao hàng</Typography>
        </Grid>

        {
          !isDeleted && tripInfo?.deliveryTripStatusCode != 'DONE' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={saveDeliveryTripButtonHandle} >Lưu</Button>
          </Grid>
        }

        {
          !isDeleted && tripInfo?.deliveryTripStatusCode != 'DONE' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={deleteButtonHandle}>Hủy bỏ</Button>
          </Grid>
        }

        {
          !isDeleted && tripInfo?.deliveryTripStatusCode != 'DONE' &&
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={autoRouteButtonHandle}>Auto route</Button>
          </Grid>
        }

        {
          !isDeleted && runnedAutoRoute && 
          <Grid className={classes.buttonWrap}>
            <Button variant="contained" className={classes.addButton} 
              type="submit" onClick={() => setShowRouteMapModal(true)}>Xem bản đồ hành trình</Button>
          </Grid>
        }

      </Grid>
    </Box>

    <Box className={classes.bodyBox}>
      <Box className={classes.formWrap}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày tạo chuyến giao hàng
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.createdStamp}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Ngày cập nhật gần nhất
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.lastUpdatedStamp}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Người tạo 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.createdBy}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Nhân viên giao hàng 
              </Box>
              <Select onChange={(e) => setSelectedDeliveryPersonId(e.target.value)}>
                {
                  deliveryPersons.length > 0 &&
                  deliveryPersons.map(person => 
                    <MenuItem key={person.userLoginId}
                      value={person.userLoginId}>{person.fullName}</MenuItem>)
                }
              </Select>
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Tổng quãng đường 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.distance}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Số điểm cần giao hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={maxSequence}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Kho lấy hàng 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={selectedWarehouseName}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>

          <Grid item xs={6}>
            <Box className={classes.inputWrap}>
              <Box className={classes.labelInput}>
                Trạng thái 
              </Box>
              <TextField
                fullWidth
                variant="outlined"
                size="small"
                value={tripInfo?.deliveryTripStatus}
                InputProps={{
                  readOnly: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>

      <StandardTable
        title="Danh sách sản phẩm"
        hideCommandBar={true}
        columns={[
          { title: "Số thứ tự giao hàng", field: "sequence" },
          { title: "Sản phẩm", field: "productName" },
          { title: "Số lượng", field: "quantity" },
          { title: "Kệ hàng", field: "bayCode" },
          { title: "Lô", field: "lotId" },
          { title: "Kho", field: "warehouseName" },
          { title: "Địa chỉ nhận hàng", field: "customerAddressName" }
        ]}
        actions={!isDeleted && [
          {
            icon: () => <AddIcon onClick={() => setShowAssignedItemsModal(true)} />,
            tooltip: "Thêm sản phẩm vào chuyến giao hàng",
            isFreeAction: true
          }
        ]}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        data={deliveryItemsTableData}
        editable={{
          onRowDelete: oldData => new Promise((resolve, reject) => {
            setTimeout(() => {
              console.log("Old data => ", oldData);
              if (oldData.deliveryTripItemId !== undefined) {
                // nếu đã lưu delivery item vào data base
                // thì xóa các delivery item này
                // và cập nhật lại quantity của assigned_order_item
                console.log("Delete from database");
                request(
                  "put",
                  API_PATH.DELIVERY_MANAGER_ASSIGN_ORDER_ITEM,
                  (res) => {
                    setCreatedItemsTableData([...createdItemsTableData, res.data]);
                  },
                  {
                    500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
                  },
                  {
                    assignOrderItemId: oldData.assignOrderItemId,
                    quantity: oldData.quantity,
                    deliveryTripItemId: oldData.deliveryTripItemId
                  }
                );
              } else {
                // nếu chưa lưu delivery item vào database
                // thì restore created items từ rawCreatedItemsTableData
                var newCreatedItemsTableData = rawCreatedItemsTableData;
                for (var i = 0; i < newCreatedItemsTableData.length; i++) {
                  if (newCreatedItemsTableData[i].assignOrderItemId == oldData.assignOrderItemId) {
                      var adder = newCreatedItemsTableData[i];
                      adder.quantity = parseFloat(adder.quantity);
                      setCreatedItemsTableData([...createdItemsTableData, adder])
                      break;
                    }
                }
              }
              
              const dataDelete = [...deliveryItemsTableData];
              const index = oldData.tableData.id;
              dataDelete.splice(index, 1);
              setDeliveryItemsTableData([...dataDelete]);
              setMaxSequence(maxSequence - 1);
              resolve();
            });
          })
        }}
      />
    </Box>
  </Fragment>)
}

export default DeliveryTripDetail;