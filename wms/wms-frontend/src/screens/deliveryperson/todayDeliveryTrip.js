import { request } from "api";
import StandardTable from "components/StandardTable";
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { useRouteMatch } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";

const TodayDeliveryTrip = () => {
  
  const [tripTableData, setTripTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const { path } = useRouteMatch();

  useEffect(() => {

    async function fetchData() {
      await request(
        "get",
        API_PATH.DELIVERY_PERSON_TODAY,
        (res) => {
          setTripTableData(res.data);
        }
      )
    }

    fetchData();

  }, []);

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <StandardTable
      title="Danh sách chuyến giao hàng cần thực hiện hôm nay"
      columns={[
        { title: "Mã chuyến", field: "deliveryTripId" },
        { title: "Ngày tạo", field: "createdStamp" },
        { title: "Trạng thái", field: "deliveryTripStatus" },
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
  </Fragment>);
}

export default TodayDeliveryTrip;