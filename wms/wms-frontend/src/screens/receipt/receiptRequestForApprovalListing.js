import { request } from "api";
import StandardTable from "components/table/StandardTable";
import { useRouteMatch } from "react-router-dom";
import { API_PATH } from "../apiPaths";

import { Fragment, useState, useEffect } from "react";

const ReceiptRequestForApprovalListing = () => {

  const [receiptTableData, setReceiptTableData] = useState([]);
  const { path } = useRouteMatch();
  
  useEffect(() => {
    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "?status=CREATED",
      (res) => {
        setReceiptTableData(res.data);
      }
    )
  }, []);

  return <Fragment>
    <StandardTable 
      title="Phê duyệt đơn xin nhập hàng"
      columns={[
        { title: "Ngày tạo đơn", field: "createdDate" },
        { title: "Ngày muốn nhận hàng", field: "expectedReceiveDate" },
        { title: "Người tạo đơn", field: "createdBy" }
      ]}
      data={receiptTableData}
      options={{
        selection: false,
        pageSize: 20,
        search: true,
        sorting: true,
      }}
      hideCommandBar={true}
      onRowClick={ (event, rowData) => {
        window.location.href = `${path}/${rowData.receiptRequestId}`;
      } }
    />
  </Fragment>
}

export default ReceiptRequestForApprovalListing;