import { request } from "api";
import StandardTable from "components/table/StandardTable";
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";

const TodayDeliveryTrip = () => {
  
  const [tripTableData, setTripTableData] = useState([]);
  const history = useHistory();
  const { path } = useRouteMatch();

  useEffect(() => {

    async function fetchData() {
      request(
        "get",
        API_PATH.DELIVERY_PERSON_TODAY,
        (res) => {
          setTripTableData(res.data);
        }
      )
    }

    fetchData();

  }, []);

  return <Fragment>
    <StandardTable
    title="Danh sách chuyến giao hàng cần thực hiện hôm nay"
      hideCommandBar={true}
      columns={[
        { title: "Mã chuyến", field: "deliveryTripId" },
        { title: "Ngày tạo", field: "createdStamp" },
        { title: "Trạng thái", field: "status" },
        { title: "Mã đợt giao hàng", field: "shipmentId"}
      ]}
      options={{
        selection: false,
        pageSize: 10,
        search: true,
        sorting: true,
      }}
      data={tripTableData}
      onRowClick={(event, rowData) => {
        window.location.href = `${path}/${rowData.deliveryTripId}`;
      }}
    />
  </Fragment>
}

export default TodayDeliveryTrip;