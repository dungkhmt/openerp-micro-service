import { Fragment, useEffect, useState } from "react";
import { request } from "api";
import { API_PATH } from "../apiPaths";
import StandardTable from "components/table/StandardTable";

const AdminOrderListing = () => {

  const [orderTableData, setOrderTableData] = useState([]);

  useEffect(() => {
    async function fetchData() {
      request(
        "get",
        API_PATH.ADMIN_SALE_ORDER,
        (res) => {
          setOrderTableData(res.data);
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
        title="Danh sách đơn bán hàng"
        columns={columns}
        data={orderTableData}
        hideCommandBar={true}
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
    </Fragment>
  );
}

export default AdminOrderListing;