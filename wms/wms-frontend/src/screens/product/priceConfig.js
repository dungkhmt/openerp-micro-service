import { Grid, Modal } from "@mui/material";
import { Box } from "@mui/system";
import { request } from "api";
import StandardTable from "components/table/StandardTable";
import { errorNoti } from "utils/notification";
import useStyles from "screens/styles";
import { API_PATH } from "../apiPaths";

import { Fragment, useState, useEffect } from "react";

const PriceHistory = ( { data } ) => {
  const [historyPricesArr, setHistoryPricesArr] = useState(data?.historyPrices == null ? [] : data?.historyPrices);
  const classes = useStyles();

  const [columns, setColumns] = useState([
    { title: "Giá bán", field: "price", type: "numeric" },
    { title: "Ngày bắt đầu", field: "startDate", type: "date" },
    { title: "Ngày kết thúc", field: "endDate", type: "date" },
    { title: "Mô tả", field: "description" }
  ]);

  return (
    <Box>
      <StandardTable
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
              console.log("new data => ", newData);
              if (newData.startDate > newData.endDate) {
                errorNoti("Ngày bắt đầu phải trước ngày kết thúc");
                reject();
              }
              const requestBody = {
                "productId": data.productId,
                "price": newData.price,
                "startDate": newData.startDate,
                "endDate": newData.endDate,
                "description": newData.description
              }
              request(
                "put",
                API_PATH.PRODUCT_PRICE,
                (res) => {
                  console.log("Response add data => ", res);
                  if (res.status == 200) {
                    setHistoryPricesArr([...historyPricesArr, newData]);
                    resolve();
                  } else {
                    errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau");
                    reject();
                  }
                },
                {},
                requestBody
              )
            })
          }),
          onRowDelete: oldData => new Promise((resolve, reject) => {
            setTimeout(() => {
              const dataDelete = [...historyPricesArr];
              const index = oldData.tableData.id;
              dataDelete.splice(index, 1);
              request(
                "delete",
                API_PATH.PRODUCT_PRICE + "/" + oldData.productPriceId,
                (res) => {
                  if (res.status == 200) {
                    setHistoryPricesArr([...dataDelete]);
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

  useEffect(() => {
    request(
      "get",
      API_PATH.PRODUCT_PRICE,
      (res) => {
        setPriceTableData(res.data);
      }
    )
  }, []);

  const columns = [
    { title: "Tên sản phẩm", field: "productName" }, 
    { title: "Giá bán hiện tại", field: "currPrice" },
  ];
  
  return (<Fragment>
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
              pageSize: 20,
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

export default PriceConfig;