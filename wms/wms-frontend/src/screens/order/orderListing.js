import { Fragment, useEffect, useState } from "react";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import StandardTable from "components/StandardTable";
import { ORDER_STATUS_CODE } from "components/constants";
import { useRouteMatch } from "react-router-dom";
import { convertToVNDFormat } from "screens/utils/utils";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { Box, Tab } from "@mui/material";

const AdminOrderListing = () => {
  const { path } = useRouteMatch();

  const [orderTableData, setOrderTableData] = useState([]);
  const [processedOrderTableData, setProcessedOrderTableData] = useState([]);
  const [deliveryBillTableData, setDeliveryBillTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

  const [value, setValue] = useState('1');

  useEffect(() => {
    async function fetchData() {
      await request(
        "get",
        API_PATH.ADMIN_SALE_ORDER + `?orderStatus=${ORDER_STATUS_CODE.DELIVERING_A_PART},${ORDER_STATUS_CODE.APPROVED}`,
        (res) => {
          var data = res.data;
          for (var i = 0; i < data.length; i++) {
            const cost = data[i]?.totalOrderCost;
            const costFormated = convertToVNDFormat(cost);
            data[i].totalOrderCost = costFormated;
          }
          setOrderTableData(data);
        }
      );

      await request(
        "get",
        API_PATH.ADMIN_SALE_ORDER + `?orderStatus=${ORDER_STATUS_CODE.CANCELLED},${ORDER_STATUS_CODE.LAST_DELIVERING},${ORDER_STATUS_CODE.COMPLETED},${ORDER_STATUS_CODE.SUCCESS},${ORDER_STATUS_CODE.FAIL},${ORDER_STATUS_CODE.DISTRIBUTED}`,
        (res) => {
          var data = res.data;
          for (var i = 0; i < data.length; i++) {
            const cost = data[i]?.totalOrderCost;
            const costFormated = convertToVNDFormat(cost);
            data[i].totalOrderCost = costFormated;
          }
          setProcessedOrderTableData(data);
        }
      );

      await request(
        'get',
        API_PATH.DELIVERY_BILL,
        (res) => {
          setDeliveryBillTableData(res.data);
        }
      )

      setLoading(false);
    }

    fetchData();
  }, []);

  const columns = [
    { title: "Ngày tạo đơn", field: "createdOrderDate" }, 
    { title: "Loại đơn hàng", field: "orderType" },
    { title: "Trạng thái", field: "status" },
    { title: "Tổng giá trị", field: "totalOrderCost" }
  ]

  return (
    isLoading ? <LoadingScreen /> :
    <Fragment>
      <TabContext value={value}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={(event, newValue) => setValue(newValue)} >
            <Tab label="Đơn xuất hàng cần xử lý" value="1" />
            <Tab label="Đơn xuất hàng đã xử lý" value="2" />
            <Tab label="Phiếu xuất hàng" value="3" />
          </TabList>
        </Box>
        <TabPanel value="1">
          <StandardTable 
            title="Danh sách đơn xuất hàng cần xử lý"
            columns={columns}
            data={orderTableData}
            hideCommandBar={true}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={(event, rowData) => {
              window.location.href = `${path}/${rowData.orderId}`;
            }}
          />
        </TabPanel>
        <TabPanel value="2">
          <StandardTable 
            title="Danh sách đơn xuất hàng đã xử lý"
            columns={columns}
            data={processedOrderTableData}
            hideCommandBar={true}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={(event, rowData) => {
              window.location.href = `${path}/${rowData.orderId}`;
            }}
          />
        </TabPanel>
        <TabPanel value="3">
          <StandardTable 
            title="Danh sách phiếu xuất hàng"
            columns={[
              { title: "Mã phiếu", field: "deliveryBillId" }, 
              { title: "Người tạo", field: "createdBy" },
              { title: "Ngày tạo phiếu", field: "createdStampStr" }
            ]}
            data={deliveryBillTableData}
            hideCommandBar={true}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            onRowClick={(event, rowData) => {
              window.location.href = `${path.replace('/orders', '/delivery-bill')}/${rowData.deliveryBillId}`;
            }}
          />
        </TabPanel>
      </TabContext>
    </Fragment>
  );
}

const SCR_ID = "SCR_WMSv2_ORDER_LISTING";
export default withScreenSecurity(AdminOrderListing, SCR_ID, true);