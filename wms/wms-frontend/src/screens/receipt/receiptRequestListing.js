// Trang liệt kê danh sách yêu cầu nhập hàng
// được tạo bởi actor là quản lý bán hàng
// chỉ liệt kê các yêu cầu nhập hàng do user này tạo

import { request } from "api";
import StandardTable from "components/StandardTable";
import { Link } from "react-router-dom";
import { API_PATH } from "../apiPaths";
import AddIcon from '@mui/icons-material/Add';
import { useRouteMatch } from "react-router-dom";
import { convertTimeStampToDate } from "screens/utils/utils";

import { Fragment, useState, useEffect } from "react";
import LoadingScreen from "components/common/loading/loading";

const ReceiptRequestListing = () => {
  const { path } = useRouteMatch();

  const [receiptTableData, setReceiptTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async() => {
      await request(
        "get",
        API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST,
        (res) => {
          const data = [];
          for (var i = 0; i < res.data?.length; i++) {
            var tempData = res.data[i];
            tempData.createdDate = convertTimeStampToDate(tempData?.createdDate);
            data.push(tempData);
          }
          setReceiptTableData(res.data);
        }
      );
      setLoading(false);
    }

    fetchData();
  }, []);

  const columns = [
    { title: "Ngày tạo đơn", field: "createdDate" },
    { title: "Trạng thái", field: "status" },
    { title: "Người phê duyệt", field: "approvedBy" },
    { title: "Người hủy", field: "cancelledBy" }
  ];

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <StandardTable
      title="Danh sách yêu cầu nhập hàng"
      columns={columns}
      data={receiptTableData}
      options={{
        selection: false,
        pageSize: 20,
        search: true,
        sorting: true,
      }}
      onRowClick={ (event, rowData) => {
        window.location.href = `${path}/${rowData.receiptRequestId}`;
      } }
      actions={[
        {
          icon: <Link to={`${path}/create`}>
            <AddIcon />
          </Link>,
          tooltip: "Tạo yêu cầu nhập hàng mới",
          isFreeAction: true
        }
      ]}
    />
  </Fragment>);
}

export default ReceiptRequestListing;