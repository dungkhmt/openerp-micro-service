import { request } from "api";
import { API_PATH } from "../apiPaths";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import StandardTable from "components/StandardTable";
import { Fragment, useState, useEffect } from "react";

const ReceiptRequestProcessListing = () => {

  const [receiptTableData, setReceiptTableData] = useState([]);
  const [processedReceiptTableData, setProcessedReceiptTableData] = useState([]);
  const { path } = useRouteMatch();

  useEffect(() => {
    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "?status=APPROVED,IN_PROGRESS",
      (res) => {
        setReceiptTableData(res.data);
      }
    );

    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "?status=CANCELLED,COMPLETED",
      (res) => {
        setProcessedReceiptTableData(res.data);
      }
    )
  }, []);

  return <Fragment>
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
  </Fragment>
}

export default ReceiptRequestProcessListing;