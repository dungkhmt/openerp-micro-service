import { Fragment, useEffect, useState } from "react";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import StandardTable from "components/table/StandardTable";
import { ORDER_STATUS_CODE } from "components/constants";
import { useRouteMatch } from "react-router-dom";

const AdminOrderListing = () => {
  const { path } = useRouteMatch();

  const [orderTableData, setOrderTableData] = useState([]);
  const [processedOrderTableData, setProcessedOrderTableData] = useState([]);

  useEffect(() => {
    async function fetchData() {
      request(
        "get",
        API_PATH.ADMIN_SALE_ORDER + `?orderStatus=${ORDER_STATUS_CODE.DELIVERING_A_PART},${ORDER_STATUS_CODE.APPROVED}`,
        (res) => {
          setOrderTableData(res.data);
        }
      );

      request(
        "get",
        API_PATH.ADMIN_SALE_ORDER + `?orderStatus=${ORDER_STATUS_CODE.CANCELLED},${ORDER_STATUS_CODE.LAST_DELIVERING},${ORDER_STATUS_CODE.COMPLETED}`,
        (res) => {
          setProcessedOrderTableData(res.data);
        }
      )
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
    <Fragment>
      <StandardTable 
        title="Danh sách đơn bán hàng cần xử lý"
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

      <StandardTable 
        title="Danh sách đơn bán hàng đã xử lý"
        columns={columns}
        data={processedOrderTableData}
        hideCommandBar={true}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </Fragment>
  );
}

export default AdminOrderListing;