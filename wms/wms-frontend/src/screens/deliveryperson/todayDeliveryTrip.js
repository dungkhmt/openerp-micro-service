// import { request } from "api";
// import StandardTable from "components/StandardTable";
// import { Fragment, useEffect, useState } from "react"
// import { API_PATH } from "screens/apiPaths";
// import { useRouteMatch } from "react-router-dom";
// import LoadingScreen from "components/common/loading/loading";
// import withScreenSecurity from "components/common/withScreenSecurity";

// const TodayDeliveryTrip = () => {
  
//   const [tripTableData, setTripTableData] = useState([]);
//   const [isLoading, setLoading] = useState(true);
//   const { path } = useRouteMatch();

//   useEffect(() => {

//     async function fetchData() {
//       await request(
//         "get",
//         API_PATH.DELIVERY_PERSON_TODAY,
//         (res) => {
//           setTripTableData(res.data);
//         }
//       )

//       setLoading(false);
//     }

//     fetchData();

//   }, []);

//   return (
//   isLoading ? <LoadingScreen /> :
//   <Fragment>
//     <StandardTable
//       title="Danh sách chuyến giao hàng cần thực hiện hôm nay"
//       columns={[
//         { title: "Mã chuyến", field: "deliveryTripId" },
//         { title: "Ngày tạo", field: "createdStamp" },
//         { title: "Trạng thái", field: "deliveryTripStatus" },
//         { title: "Mã đợt giao hàng", field: "shipmentId"}
//       ]}
//       options={{
//         selection: false,
//         pageSize: 10,
//         search: true,
//         sorting: true,
//       }}
//       data={tripTableData}
//       onRowClick={(event, rowData) => {
//         window.location.href = `${path}/${rowData.deliveryTripId}`;
//       }}
//     />
//   </Fragment>);
// }

// const SCR_ID = "SCR_WMSv2_TODAY_DELIVERY_TRIP";
// export default withScreenSecurity(TodayDeliveryTrip, SCR_ID, true);


import { request } from "api";
import StandardTable from "components/StandardTable";
import { Fragment, useEffect, useState } from "react"
import { API_PATH, API_PATH_2 } from "screens/apiPaths";
import { useRouteMatch } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { useHistory } from "react-router";


const TodayDeliveryTrip = () => {

  const history = useHistory();
  const [tripTableData, setTripTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const { path } = useRouteMatch();

  useEffect(() => {

    async function fetchData() {
      await request(
        "get",
        API_PATH_2.DELIVERY_PERSON_TODAY,
        (res) => {
          setTripTableData(res.data);
        }
      )

      setLoading(false);
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
        history.push(`${path}/${rowData.deliveryTripId}`);
      }}
    />
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_TODAY_DELIVERY_TRIP";
export default withScreenSecurity(TodayDeliveryTrip, SCR_ID, true);
