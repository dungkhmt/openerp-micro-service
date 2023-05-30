import { Box, Modal } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable"
import { Fragment } from "react";
import { useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { convertToVNDFormat } from "screens/utils/utils";

const ProductsReport = () => {

  const [productsTableData, setProductsTableData] = useState([]);
  const [productHistoryMap, setProductHistoryMap] = useState({});
  const [productHistory, setProductHistory] = useState([]);
  const [isOpenModal, setOpenModal] = useState(false);

  useEffect(() => {
    request(
      'get',
      API_PATH.PRODUCTS_REPORT,
      (res) => {
        var tableData = res?.data;
        for (var i = 0; i < tableData.length; i++) {
          tableData[i].totalImportPrice = convertToVNDFormat(tableData[i].totalImportPrice);
          tableData[i].totalExportPrice = convertToVNDFormat(tableData[i].totalExportPrice);
          tableData[i].price = convertToVNDFormat(tableData[i].price); 
        }
        setProductsTableData(tableData);
      }
    );

    request(
      'get',
      API_PATH.PRODUCT_HISTORY_REPORT,
      (res) => {
        setProductHistoryMap(res?.data);
      }
    )
  }, []);

  const columns = [
    { title: "Tên sản phẩm", field: "productName" }, 
    { title: "Số lượng hàng tồn", field: "totalQuantity" },
    { title: "Tổng giá trị hàng khi nhập", field: "totalImportPrice" },
    { title: "Tổng giá trị hàng khi bán", field: "totalExportPrice" },
    { title: "Giá bán hiện tại", field: "price" } ,
    { title: "", field: "", 
      buttonOnclickText: "Lịch sử",  
      buttonOnclickHandle: (rowData) => {
        console.log("Row data => ", rowData);
        setOpenModal(true);
        setProductHistory(productHistoryMap[rowData.productId]);
      }
    }
  ]

  return (
  <Fragment>
    <Modal open={isOpenModal}
          onClose={() => setOpenModal(!isOpenModal)}
          aria-labelledby="modal-modal-title"
          aria-describedby="modal-modal-description"
        >
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
            <StandardTable
              title="Lịch sử sản phẩm"
              data={productHistory}
              columns={[
                { title: "Sản phẩm", field: "productName" },
                { title: "Số lượng", field: "quantity" },
                { title: "Thời điểm", field: "effectiveDateStr" },
                { title: "Hành động", field: "type" }
              ]}
              options={{
                selection: false,
                pageSize: 10,
                search: true,
                sorting: true,
              }}
            />
          </Box>
      </Modal>
      <StandardTable
      title="Danh sách hàng tồn kho"
      columns={columns}
      hideCommandBar={true}
      data={productsTableData}
      options={{
        selection: false,
        pageSize: 20,
        search: true,
        sorting: true,
      }} />
  </Fragment>
  );
}

export default ProductsReport;