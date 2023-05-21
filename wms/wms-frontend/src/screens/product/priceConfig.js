import { Grid, Modal, TextField } from "@mui/material";
import { Box } from "@mui/system";
import { request } from "api";
import StandardTable from "components/StandardTable";
import { errorNoti } from "utils/notification";
import useStyles from "screens/styles";
import { API_PATH } from "../apiPaths";

import { Fragment, useState, useEffect } from "react";
import { convertToVNDFormat } from "screens/utils/utils";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const PriceHistory = ( { data } ) => {
  const [historyPricesArr, setHistoryPricesArr] = useState(data?.historyPrices == null ? [] : data?.historyPrices);
  const classes = useStyles();
  const [newPrice, setNewPrice] = useState(0);
  const [newStartDate, setNewStartDate] = useState(null);
  const [newEndDate, setNewEndDate] = useState(null);
  const [newDescription, setNewDescription] = useState(null);

  const columns = [
    { title: "Giá bán *", field: "price",
      editComponent: <TextField type="number" value={newPrice} onChange={(e) => setNewPrice(e.target.value)} /> },
    { title: "Ngày bắt đầu *", field: "startDate",
      editComponent: <TextField type="date" value={newStartDate} onChange={(e) => setNewStartDate(e.target.value)} />  },
    { title: "Ngày kết thúc", field: "endDate",
      editComponent: <TextField type="date" value={newEndDate} onChange={(e) => setNewEndDate(e.target.value)} /> },
    { title: "Mô tả", field: "description",
      editComponent: <TextField value={newDescription} onChange={(e) => setNewDescription(e.target.value)} /> }
  ];

  return (
    <Box>
      <StandardTable
        rowKey="productPriceId"
        title={"Cấu hình giá cho " + data?.productName}
        columns={columns}
        data={historyPricesArr}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        editable={{
          onRowAdd: newData => new Promise((resolve, reject) => {
            setTimeout(() => {
              if (newStartDate == null) {
                errorNoti("Vui lòng nhập giá trị Ngày bắt đầu");
                reject();
              }
              if (newPrice <= 0) {
                errorNoti("Vui lòng kiểm tra lại giá trị giá bán");
                reject();
              }
              if (newStartDate > newEndDate) {
                errorNoti("Ngày bắt đầu phải trước ngày kết thúc");
                reject();
              } else {
                const requestBody = {
                  "productId": data.productId,
                  "price": newPrice,
                  "startDate": newStartDate,
                  "endDate": newEndDate,
                  "description": newDescription
                }
                request(
                  "put",
                  API_PATH.PRODUCT_PRICE,
                  (res) => {
                    console.log("Response add data => ", res);
                    if (res.status == 200) {
                      setHistoryPricesArr([...historyPricesArr, requestBody]);
                      resolve();
                    } else {
                      errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau");
                      reject();
                    }
                  },
                  {},
                  requestBody
                )
                resolve();
              }
            })
          }),
          onRowDelete: selectedIds => new Promise((resolve, reject) => {
            setTimeout(() => {
              request(
                "delete",
                `${API_PATH.PRODUCT_PRICE}/${selectedIds.join(',')}`,
                (res) => {
                  if (res.status == 200) {
                    const dataDelete = historyPricesArr.filter(
                      history => !selectedIds.includes(history["productPriceId"])
                    );
                    setHistoryPricesArr(dataDelete);
                    resolve();
                  } else {
                    errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau");
                    reject();
                  }
                }
              )
            });
          })
        }}
      />
    </Box>
  );
}
 
const PriceConfig = () => {
  const classes = useStyles();
  const [priceHistory, setPriceHistory] = useState({});
  const [isOpenModal, setOpenModal] = useState(false);
  const [priceTableData, setPriceTableData] = useState([]);

  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.PRODUCT_PRICE,
        (res) => {
          var data = res.data;
          for (var i = 0; i < data?.length; i++) {
            if (data[i].currPrice == null) {
              continue;
            }
            data[i].currPrice = convertToVNDFormat(data[i].currPrice);
          }
          setPriceTableData(data);
        }
      );

      setLoading(false);
    }

    fetchData();
  }, []);

  const columns = [
    { title: "Tên sản phẩm", field: "productName" }, 
    { title: "Giá bán hiện tại", field: "currPrice" },
  ];
  
  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <Modal 
      open={isOpenModal}
      onClose={() => {
        setOpenModal(!isOpenModal);
        window.location.reload();
      }}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description">
      <Box sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          width: '75%',
          height: '75%',
          transform: 'translate(-50%, -50%)',
          bgcolor: 'background.paper',
          border: '2px solid #000',
          boxShadow: 24,
          p: 4,
        }}>
        <PriceHistory data={priceHistory} />
      </Box>
    </Modal>
    <Box className={classes.formWrap}>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <StandardTable
            title={"Cấu hình giá sản phẩm"}
            columns={columns}
            data={priceTableData}
            options={{
              selection: true,
              pageSize: 10,
              search: true,
              sorting: true,
            }}
            onRowClick={ (event, rowData) => {
                console.log("On row click => row data: ", rowData);
                setPriceHistory(rowData);
                setOpenModal(true)
              }
            }
          />
        </Grid>
      </Grid>
    </Box>
  </Fragment>);
}


const SCR_ID = "SCR_WMSv2_PRICE_CONFIG";
export default withScreenSecurity(PriceConfig, SCR_ID, true);
