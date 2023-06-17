import { request } from "api";
import { API_PATH } from "../apiPaths";
import { useRouteMatch } from "react-router-dom";
import StandardTable from "components/StandardTable";
import { Fragment, useState, useEffect } from "react";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Box, Tab } from "@mui/material";
import { TabContext, TabList, TabPanel } from "@mui/lab";

const ReceiptRequestProcessListing = () => {

  const [receiptTableData, setReceiptTableData] = useState([]);
  const [processedReceiptTableData, setProcessedReceiptTableData] = useState([]);
  const [receiptBillTableData, setReceiptBillTableData] = useState([]);
  const { path } = useRouteMatch();
  const [isLoading, setLoading] = useState(true);

  const [tabValue, setTabValue] = useState('1');

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "?status=APPROVED,IN_PROGRESS",
        (res) => {
          setReceiptTableData(res.data);
        }
      );

      await request(
        "get",
        API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "?status=CANCELLED,COMPLETED",
        (res) => {
          setProcessedReceiptTableData(res.data);
        }
      );

      await request(
        'get',
        API_PATH.ALL_RECEIPT_BILLS,
        (res) => {
          setReceiptBillTableData(res.data);
        }
      )

      setLoading(false);
    }

    fetchData();
  }, []);

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <Box>
      <TabContext value={tabValue}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={(event, value) => setTabValue(value)}>
            <Tab label="Đơn nhập hàng cần xử lý" value='1'></Tab>
            <Tab label="Đơn nhập hàng đã xử lý" value='2'></Tab>
            <Tab label="Phiếu nhập hàng" value='3'></Tab>
          </TabList>
        </Box>
        <TabPanel value="1">
          <StandardTable 
            title="Danh sách đơn nhập hàng cần xử lý"
            columns={[
              { title: "Ngày tạo đơn", field: "createdDate" },
              { title: "Trạng thái", field: "status" },
              { title: "Ngày muốn nhận hàng", field: "expectedReceiveDate" },
              { title: "Người tạo đơn", field: "createdBy" },
              { title: "Người phê duyệt", field: "approvedBy" }
            ]}
            data={receiptTableData}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={ (event, rowData) => {
              window.location.href = `${path}/${rowData.receiptRequestId}`;
            } }
          />
        </TabPanel>
        <TabPanel value="2">
          <StandardTable 
            title="Danh sách đơn nhập hàng đã xử lý"
            columns={[
              { title: "Ngày tạo đơn", field: "createdDate" },
              { title: "Trạng thái", field: "status" },
              { title: "Người tạo đơn", field: "createdBy" },
              { title: "Người phê duyệt", field: "approvedBy" },
              { title: "Người hủy", field: "cancelledBy" },
              { title: "Cập nhật lần cuối", field: "lastUpdateStamp" }
            ]}
            data={processedReceiptTableData}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={ (event, rowData) => {
              window.location.href = `${path}/${rowData.receiptRequestId}`;
            } }
          />
        </TabPanel>
        <TabPanel value="3">
          <StandardTable 
            title="Danh sách phiếu nhập hàng"
            columns={[
              { title: "Mã phiếu", field: "receiptBillId" }, 
              { title: "Tổng giá trị", field: "totalPrice" },
              { title: "Ngày tạo", field: "createdStampStr" },
              { title: "Mô tả", field: "description" }
            ]}
            data={receiptBillTableData}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={ (event, rowData) => {
              window.location.href = `${path.replace('process-receipts', 'receipt-bill')}/${rowData.receiptBillId}`;
            } }
          />
        </TabPanel>
      </TabContext>
    </Box>
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_RECEIPT_REQUEST_PROCESS_LISTING";
export default withScreenSecurity(ReceiptRequestProcessListing, SCR_ID, true);
