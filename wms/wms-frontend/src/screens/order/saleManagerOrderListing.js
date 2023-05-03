import { useRouteMatch } from "react-router-dom";
 import { Fragment, useEffect, useState } from "react"
import StandardTable from "components/table/StandardTable"
import { request } from "api";
import { API_PATH } from "screens/apiPaths";
import { ORDER_STATUS_CODE } from "components/constants";
import { convertTimeStampToDate } from "screens/utils/utils";

const SaleManagerOrderListing = () => {
  const { path } = useRouteMatch();
  const [ordersTableData, setOrdersTableData] = useState([]);
  
  useEffect(() => {
    request(
      "get",
      // API_PATH.ADMIN_SALE_ORDER + `?orderStatus=${ORDER_STATUS_CODE.CREATED}`,
      API_PATH.ADMIN_SALE_ORDER,
      (res) => {
        var data = res.data;
        for (var i = 0; i < data.length; i++) {
          const createdTimestamp = data[i]?.createdOrderDate;
          const dateFormated = convertTimeStampToDate(createdTimestamp);
          data[i].createdOrderDate = dateFormated;
        }
        setOrdersTableData(data); 
      }
    );
  }, []);

  return <Fragment>
    <StandardTable
      title="Danh sách đơn hàng"
      columns={[
        { title: "Ngày tạo đơn", field: "createdOrderDate" }, 
        { title: "Loại đơn hàng", field: "orderType" },
        { title: "Trạng thái", field: "status" },
        { title: "Tổng giá trị", field: "totalOrderCost" }
      ]}
      data={ordersTableData}
      options={{
        selection: false,
        pageSize: 10,
        search: true,
        sorting: true,
      }}
      hideCommandBar={true}
      onRowClick={(event, rowData) => {
        window.location.href = `${path}/${rowData.orderId}`;
      }}
    />
  </Fragment>
}

export default SaleManagerOrderListing;