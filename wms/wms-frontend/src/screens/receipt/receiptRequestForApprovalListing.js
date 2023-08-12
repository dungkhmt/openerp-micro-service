import { request } from "api";
import StandardTable from "components/StandardTable";
import { useRouteMatch } from "react-router-dom";
import { API_PATH } from "../apiPaths";

import { Fragment, useState, useEffect } from "react";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const ReceiptRequestForApprovalListing = () => {

  const [receiptTableData, setReceiptTableData] = useState([]);
  const { path } = useRouteMatch();
  const [isLoading, setLoading] = useState(true);
  
  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST_APPROVAL_LISTING + "?status=CREATED",
        (res) => {
          setReceiptTableData(res.data);
        }
      );

      setLoading(false);
    }

    fetchData();
  }, []);

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <StandardTable 
      title="Phê duyệt yêu cầu nhập hàng"
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
      onRowClick={ (event, rowData) => {
        window.location.href = `${path}/${rowData.receiptRequestId}`;
      } }
    />
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_RECEIPT_REQUEST_FOR_APPROVER";
export default withScreenSecurity(ReceiptRequestForApprovalListing, SCR_ID, true);
