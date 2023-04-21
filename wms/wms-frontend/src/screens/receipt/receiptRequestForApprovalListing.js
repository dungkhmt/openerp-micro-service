import { request } from "api";
import StandardTable from "components/table/StandardTable";
import { useRouteMatch } from "react-router-dom";
import { API_PATH } from "../apiPaths";

import { Fragment, useState, useEffect } from "react";
import { convertTimeStampToDate } from "screens/utils/utils";

const ReceiptRequestForApprovalListing = () => {

  const [receiptTableData, setReceiptTableData] = useState([]);
  const { path } = useRouteMatch();
  
  useEffect(() => {
    request(
      "get",
      API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST_APPROVAL_LISTING + "?status=CREATED",
      (res) => {
        var data = res.data;
        for (var i = 0; i < data.length; i++) {
          const createdTimestamp = data[i]?.createdDate;
          const dateFormated = convertTimeStampToDate(createdTimestamp);
          data[i].createdDate = dateFormated;

          const expectedReceiveTimestamp = data[i]?.expectedReceiveDate;
          const expectedReceiveDate = convertTimeStampToDate(expectedReceiveTimestamp);
          data[i].expectedReceiveDate = expectedReceiveDate;
        }
        setReceiptTableData(data);
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
        pageSize: 10,
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