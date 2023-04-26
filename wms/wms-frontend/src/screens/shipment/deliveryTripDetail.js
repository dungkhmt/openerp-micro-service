import AddIcon from '@mui/icons-material/Add';
import { Box, Button, Grid, MenuItem, Modal, Select, TextField, Typography } from "@mui/material";
import { request } from "api";
import { WarehouseDropDown } from "components/table/DropDown";
import StandardTable from "components/table/StandardTable";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";
import useStyles from 'screens/styles.js';
import { errorNoti, successNoti } from 'utils/notification';

const DeliveryTripDetail = ( props ) => {
  const tripId = props.match?.params?.id;
  const classes = useStyles();

  const [tripInfo, setTripInfo] = useState({});
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
  const [maxSequence, setMaxSequence] = useState(0);

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
        setCreatedItemsTableData(res.data);
      }
    );
  }, []);

  useEffect(() => {
    // filter created assigned items table data
    // that match with selected warehouse id
    if (selectedWarehouseId != null) {
      const filterTableData = createdItemsTableData.filter(
        item => item.warehouseId == selectedWarehouseId);
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
    for (var i = 0; i < deliveryItemsTableData.length; i++) {
      if (deliveryItemsTableData[i].assignOrderItemId 
        == newDeliveryItem.assignOrderItemId) {
          var localDeliveryItem = deliveryItemsTableData[i];
          const newQuantity = parseInt(localDeliveryItem.quantity) + parseInt(newDeliveryItem.quantity);
          localDeliveryItem.quantity = newQuantity;
          var newDeliveryItemsTableData = deliveryItemsTableData;
          newDeliveryItemsTableData.splice(i, 1);
          setDeliveryItemsTableData([...newDeliveryItemsTableData, localDeliveryItem]);
          updated = true;
          break;
        }
    }
    if (!updated) {
      setDeliveryItemsTableData([...deliveryItemsTableData, newDeliveryItem]);
      setMaxSequence(maxSequence + 1);
    }
    setSelectedWarehouseId(selectedAssignedItem.warehouseId);
    setSelectedWarehouseName(selectedAssignedItem.warehouseName);
    setSelectedQuantity(0);

    // update created assign items table data 
    // substract quantity 
    // or remove item if quantity if needed 
    var localSelectedItem = selectedAssignedItem;
    const newQuantity = maxQuantity - selectedQuantity;
    localSelectedItem.quantity = newQuantity;
    var index = -1;
    for (var i = 0 ; i < createdItemsTableData.length; i++) {
      if (createdItemsTableData[i].assignOrderItemId 
        == selectedAssignedItem.assignOrderItemId) {
        index = i;
        break;
      }
    }
    var newCreatedItemsTableData = createdItemsTableData;
    newCreatedItemsTableData.splice(index, 1);
    if (newQuantity > 0) {
      setCreatedItemsTableData([...newCreatedItemsTableData, localSelectedItem]);
    } else {
      setCreatedItemsTableData(newCreatedItemsTableData);
    }
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
        warehouseId: selectedWarehouseId,
        totalLocations: maxSequence,
        items: [
          ...deliveryItemsTableData
        ]
      }
    )
  }

  return <Fragment>

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
          height: '25%',
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

        <Grid className={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" onClick={saveDeliveryTripButtonHandle} >Lưu</Button>
        </Grid>
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
                    <MenuItem key={person.deliveryPersonId}
                      value={person.fullName}></MenuItem>)
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
              {/* <Select onChange={(e, v) => {
                  setSelectedWarehouseId(e.target.value);
                  setSelectedWarehouseName(v?.props?.children);
                }}
                defaultValue={""}
                value={selectedWarehouseName}>
              {
                warehouseList.length > 0 &&
                warehouseList.map(warehouse => 
                  <MenuItem key={warehouse.id} value={warehouse.id}>{warehouse.name}</MenuItem>)
              }
            </Select> */}
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
        actions={[
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
      />
    </Box>
  </Fragment>
}

export default DeliveryTripDetail;