import { Fragment, useState, useEffect } from "react";
import useStyles from "screens/styles";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import { Box, Button, Grid, Modal, TextField, Typography } from "@mui/material";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import StandardTable from "components/table/StandardTable";
import { convertTimeStampToDate } from "../utils/utils";
import { BayDropDown, WarehouseDropDown } from "components/table/DropDown";
import { errorNoti, successNoti } from "utils/notification";

const ProcessItem = ( { rowData, warehousesDetail, setProcessingItems, 
  processingItems, setOpenProcessModal, remainingItems, setRemainingItems } ) => {
  const classes = useStyles();

  const [localProcessingItems, setLocalProcessingItems] = useState([]);
  const [selectedBayId, setSelectedBayId] = useState(null);
  const [selectedBayCode, setSelectedBayCode] = useState(null);
  const [selectedWarehouseId, setSelectedWarehouseId] = useState(null);
  const [selectedWarehouseName, setSelectedWarehouseName] = useState(null);
  const [selectedWarehouse, setSelectedWarehouse] = useState(null);
  const [addedItemCount, setAddedItemCount] = useState(0);

  useEffect(() => {
    for (var i = 0; i < warehousesDetail?.length; i++) {
      if (warehousesDetail[i].id == selectedWarehouseId) {
        setSelectedWarehouse(warehousesDetail[i]);
      }
    }
  }, [selectedWarehouseId]);

  const saveButtonHandle = () => {
    setProcessingItems([...processingItems, ...localProcessingItems]);
    setOpenProcessModal(false);
    var newRemainingItems = remainingItems;
    var deleteItemId = null;
    for (var i = 0; i < newRemainingItems?.length; i++) {
      if (newRemainingItems[i].receiptItemRequestId == rowData.receiptItemRequestId) {
        const prevQuantity = newRemainingItems[i].quantity;
        const newQuantity = prevQuantity - addedItemCount;
        newRemainingItems[i].quantity = newQuantity;
        if (newQuantity == 0) {
          deleteItemId = i;
        }
        break;
      }
    }
    
    if (deleteItemId != null) {
      newRemainingItems.splice(deleteItemId, 1);
    }

    setRemainingItems(newRemainingItems);
  }

  console.log("Process item => ", warehousesDetail);
  return <Box>
    <Button variant="contained" className={classes.addButton} 
      onClick={saveButtonHandle}>Lưu</Button>
    <StandardTable
      hideCommandBar={true}
      title={`${rowData.productName} - Tổng số lượng cần xử lý ${rowData.quantity}`}
      columns={[
        { title: "Kho nhận *", field: "warehouseName",
          editComponent: props => <WarehouseDropDown
            warehouseList={warehousesDetail}
            setSelectedWarehouseId={setSelectedWarehouseId}
            setSelectedWarehouseName={setSelectedWarehouseName} /> },
        { title: "Kệ hàng *", field: "bayCode",
          editComponent: props => <BayDropDown
            selectedWarehouse={selectedWarehouse}
            setSelectedBayId={setSelectedBayId}
            setSelectedBayCode={setSelectedBayCode} /> },
        { title: "Số lô *", field: "lotId" },
        { title: "Giá nhập hàng *", field: "importPrice", type: "numeric", min: 0, max: 5 },
        { title: "Số lượng *", field: "quantity", type: "numeric" },
      ]}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
      data={localProcessingItems}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            // validate new row data
            console.log("New data => ", newData);
            const adder = {
              ...newData,
              bayCode: selectedBayCode,
              warehouseName: selectedWarehouseName,
              warehouseId: selectedWarehouseId,
              bayId: selectedBayId,
              productName: rowData.productName,
              receiptItemRequestId: rowData.receiptItemRequestId,
              productId: rowData.productId
            };
            if (adder.warehouseId == null ||
              adder.bayId == null ||
              adder.quantity == null ||
              adder.lotId == null ||
              adder.importPrice == null) {
                errorNoti("Vui lòng nhập đầy đủ thông tin");
            } else if (adder.importPrice < 0) {
              errorNoti("Giá nhập hàng không được nhỏ hơn 0");
            } else if (adder.quantity < 0) {
              errorNoti("Số lượng không được nhỏ hơn 0");
            } else {
              const total = addedItemCount + adder.quantity;
              if (total > rowData.quantity) {
                errorNoti("Bạn đã nhập quá số lượng hàng. Vui lòng kiểm tra lại");
              } else {
                setAddedItemCount(total);
                setLocalProcessingItems([...localProcessingItems, adder]);
              }
            }
            resolve();
          }, 500);
        }),
        onRowDelete: oldData => new Promise((resolve, reject) => {
          const dataDelete = [...localProcessingItems];
          const index = oldData.tableData.id;
          dataDelete.splice(index, 1);
          setLocalProcessingItems([...dataDelete]);
          resolve();
        })
      }}
    />
  </Box>
}

const ReceiptRequestProcess = ( props ) => {
  const receiptId = props.match?.params?.id;
  const classes = useStyles();

  const [receiptInfo, setReceiptInfo] = useState(null);
  const [remainingItems, setRemainingItems] = useState([]);
  const [processingItems, setProcessingItems] = useState([]);
  const [processedItems, setProcessedItems] = useState([]);

  const [isOpenProcessModal, setOpenProcessModal] = useState(false);
  const [rowData4ProcessItem, setRowData4ProcessItem] = useState(null);
  const [warehousesDetail4ProcessItem, setWarehousesDetail4ProcessItem] = useState([]);

  const [isDoneReceipt, setIsDoneReceipt] = useState(false);

  const [warehouseList, setWarehouseList] = useState([]);
  const [productList, setProductList] = useState([]);
  const history = useHistory();
  const { path } = useRouteMatch();

  useEffect(() => {
    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "/" + receiptId,
      (res) => {
        setReceiptInfo(res.data);
        if (['Đã hoàn thành', 'Đã hủy'].includes(res.data.status)) {
          setIsDoneReceipt(true);
        }
      }
    );

    request(
      "get",
      API_PATH.PROCESS_RECEIPT_REQUEST + "/" + receiptId,
      (res) => {
        setRemainingItems(res.data?.remainingItems);
        setProcessedItems(res.data?.processedItems);
      }
    );

    request(
      "get",
      API_PATH.WAREHOUSE_DETAIL,
      (res) => {
        setWarehouseList(res.data);
      }
    );

    request(
      "get",
      API_PATH.PRODUCT,
      (res) => {
        setProductList(res.data);
      }
    );
  }, []);

  const saveButtonHandle = () => {
    const isDone = remainingItems.length == 0;
    request(
      "post",
      API_PATH.PROCESS_RECEIPT_REQUEST + `/${receiptInfo.receiptRequestId}?isDone=${isDone}`,
      (res) => {
        if (res.status == 200) {
          successNoti("Lưu xử lý đơn hàng thành công");
          history.push(`${path.substring(0, path.lastIndexOf("/"))}`);
        }
      },
      {
        500: () => errorNoti("Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau")
      },
      processingItems
    );
  }

  const cancelReceiptButtonHandle = () => {
    request(
      "put",
      API_PATH.CANCEL_RECEIPT_REQUEST + `/${receiptInfo.receiptRequestId}`,
      (res) => {
        if (res.status == 200) {
          successNoti("Hủy bỏ đơn hàng thành công");
          history.push(`${path.substring(0, path.lastIndexOf("/"))}`);
        }
      }
    )
  }

  return <Fragment>
    <Modal open={isOpenProcessModal}
      onClose={() => setOpenProcessModal(!isOpenProcessModal)}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description">
      <Box sx={{
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
      }} >
        <ProcessItem remainingItems={remainingItems} setRemainingItems={setRemainingItems} setOpenProcessModal={setOpenProcessModal} processingItems={processingItems} setProcessingItems={setProcessingItems} rowData={rowData4ProcessItem} warehousesDetail={warehousesDetail4ProcessItem} />
      </Box>
    </Modal>

    <Box>
      <Grid container justifyContent="space-between" className={classes.headerBox}>
        <Typography variant="h5">Xử lý đơn nhập hàng</Typography>
        {
          !isDoneReceipt &&
          <Grid>
            <Grid className={classes.buttonWrap}>
              <Button variant="contained" className={classes.addButton}
                type="submit" onClick={saveButtonHandle} >Lưu</Button>
            </Grid>
            <Grid classNam={classes.buttonWrap}>
              <Button variant="contained" className={classes.addButton}
                type="submit" onClick={cancelReceiptButtonHandle} >Hủy đơn nhập hàng</Button>
            </Grid>
          </Grid>
        }
      </Grid>
    </Box>

    <Box className={classes.formWrap} component="form">
      <Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Box className={classes.boxInfor}>
              <Typography className={classes.inforTitle} variant="h6">
                Thông tin chung
              </Typography>
              <Grid container spacing={3} className={classes.inforWrap}>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Người tạo đơn
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdBy"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdBy} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Trạng thái
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdBy"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.status} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày tạo đơn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={convertTimeStampToDate(receiptInfo?.createdDate)} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày muốn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="expectedReceiveDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={convertTimeStampToDate(receiptInfo?.expectedReceiveDate)} />
                </Grid>
                <Grid item xs={12}>
                  <Box className={classes.labelInput}>
                    Lý do tạo đơn
                  </Box>
                  <TextField 
                    fullWidth
                    variant="outlined"
                    size="small"
                    name="createdReason"
                    multiline
                    rows={4}
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdReason}
                  />
                </Grid>
              </Grid>
            </Box>

            {
              !isDoneReceipt &&
              <Box>
                <Box className={classes.boxInfor}>
                  <StandardTable
                    title="Danh sách hàng hóa cần xử lý"
                    columns={[
                      { title: "Sản phẩm", field: "productName" },
                      { title: "Số lượng", field: "quantity" },
                      { title: "Kho nhận hàng (nếu có)", field: "warehouseName" },
                      { title: "", fileds: "productId",
                        render: (rowData) =>
                          <Button variant="contained" className={classes.addButton}
                          onClick={() => {
                            setRowData4ProcessItem(rowData);
                            // TODO: set warehouse 4 process item
                            if (rowData.warehouseId != null) {
                              for (var i = 0; i < warehouseList?.length; i++) {
                                if (warehouseList[i].id == rowData.warehouseId) {
                                  setWarehousesDetail4ProcessItem([warehouseList[i]]);
                                  break;
                                }
                              }
                            } else {
                              setWarehousesDetail4ProcessItem(warehouseList);
                            }
                            setOpenProcessModal(true);
                          }}>Xử lý</Button>
                      }
                    ]}
                    data={remainingItems}
                    options={{
                      selection: false,
                      pageSize: 5,
                      search: true,
                      sorting: true,
                    }}
                    hideCommandBar={true}
                  />
                </Box>
                <Box className={classes.boxInfor}>
                  <StandardTable
                    title="Danh sách hàng hóa đang xử lý"
                    columns={[
                      { title: "Sản phẩm", field: "productName" },
                      { title: "Số lượng", field: "quantity" },
                      { title: "Kho nhận", field: "warehouseName" },
                      { title: "Kệ hàng", field: "bayCode" },
                      { title: "Số lô", field: "lotId" },
                      { title: "Giá nhập hàng", field: "importPrice" },
                    ]}
                    data={processingItems}
                    options={{
                      selection: false,
                      pageSize: 5,
                      search: true,
                      sorting: true,
                    }}
                    hideCommandBar={true}
                    editable={{
                      onRowDelete: oldData => new Promise((resolve, reject) => {
                        // delete record from this table
                        const dataDelete = [...processingItems];
                        const index = oldData.tableData.id;
                        dataDelete.splice(index, 1);
                        setProcessingItems([...dataDelete]);
                        // update quantity from remaining item table
                        console.log("Old data => ", oldData);
                        var remainingItemTemp = remainingItems;
                        var quantityUpdated = false;
                        for (var i = 0; i < remainingItemTemp?.length; i++) {
                          if (remainingItemTemp[i]?.receiptItemRequestId == oldData.receiptItemRequestId) {
                            const newQuantity = remainingItemTemp[i].quantity + oldData.quantity;
                            remainingItemTemp[i].quantity = newQuantity;
                            quantityUpdated = true;
                            break;
                          }
                        }
                        if (!quantityUpdated) {
                          // restore record if needed
                          setRemainingItems([...remainingItems, oldData]);
                        } else {
                          setRemainingItems(remainingItemTemp);
                        }
                        resolve();
                      })
                    }}
                  />
                </Box>
              </Box>
            }

            <Box className={classes.boxInfor}>
              <StandardTable 
                hideCommandBar={true}
                columns={[
                  { title: "Sản phẩm", field: "productName" },
                  { title: "Số lượng", field: "quantity" },
                  { title: "Kho nhận", field: "warehouseName" },
                  { title: "Kệ hàng", field: "bayCode" },
                  { title: "Số lô", field: "lotId" },
                  { title: "Giá nhập hàng", field: "importPrice" },
                ]}
                title="Danh sách hàng hóa đã xử lý"
                data={processedItems}
                options={{
                  selection: false,
                  pageSize: 5,
                  search: true,
                  sorting: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Box>
  </Fragment>
}

export default ReceiptRequestProcess;