// import { TextField } from "@mui/material";
// import { request } from "api";
// import { ShipmentDropDown } from "components/table/DropDown";
// import StandardTable from "components/StandardTable";
// import { Fragment, useEffect, useState } from "react"
// import { API_PATH } from "screens/apiPaths";
// import { getCurrentDateInString } from "screens/utils/utils";
// import { errorNoti, successNoti } from "utils/notification";
// import { useHistory } from "react-router";
// import { useRouteMatch } from "react-router-dom";
// import LoadingScreen from "components/common/loading/loading";
// import withScreenSecurity from "components/common/withScreenSecurity";

// const DeliveryTripListing = () => {
//   const [userLoginId, setUserLoginId] = useState(null);
//   const [shipmentList, setShipmentList] = useState([]);
//   const [selectedShipmentId, setSelectedShipmentId] = useState(null);
//   const [tripTableData, setTripTableData] = useState([]);
//   const now = getCurrentDateInString();
//   const [isLoading, setLoading] = useState(true);

//   const history = useHistory();
//   const { path } = useRouteMatch();
  
//   useEffect(() => {
//     const fetchData = async () => {
//       await request(
//         "get",
//         API_PATH.GET_USER_LOGIN_ID,
//         (res) => {
//           setUserLoginId(res.data);
//         }
//       );

//       await request(
//         "get",
//         API_PATH.DELIVERY_MANAGER_SHIPMENT,
//         (res) => {
//           setShipmentList(res.data);
//         }
//       );
      
//       await request(
//         "get",
//         API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
//         (res) => {
//           setTripTableData(res.data);
//         }
//       );

//       setLoading(false);
//     }

//     fetchData();
//   }, []);

//   return (
//   isLoading ? <LoadingScreen /> :
//   <Fragment>
//     <StandardTable
//       rowKey="deliveryTripId"
//       title="Danh sách các chuyến giao hàng"
//       hideCommandBar={true}
//       columns={[
//         { title: "Mã chuyến", field: "deliveryTripId",
//           editComponent: <TextField InputProps={{readOnly: true}}/> },
//         { title: "Ngày tạo", field: "createdStamp",
//           editComponent: <TextField value={now}/> },
//         { title: "Người tạo", field: "createdBy", 
//           editComponent: <TextField value={userLoginId}/> }, 
//         { title: "Đợt giao hàng", field: "shipmentId",
//           editComponent: <ShipmentDropDown shipmentList={shipmentList}
//             setSelectedShipmentId={setSelectedShipmentId} />},
//         { title: "Trạng thái", field: "deliveryTripStatus",
//           editComponent: <TextField value={"Khởi tạo"} InputProps={{readOnly: true}}/>}
//       ]}
//       data={tripTableData}
//       editable={{
//         onRowAdd: newData => new Promise((resolve, reject) => {
//           setTimeout(() => {
//             request(
//               "put",
//               API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
//               (res) => {
//                 if (res.status == 200) {
//                   const adder = {
//                     deliveryTripId: res.data.deliveryTripId,
//                     createdStamp: now,
//                     createdBy: userLoginId,
//                     shipmentId: selectedShipmentId,
//                     deliveryTripStatus: "Khởi tạo"
//                   };
//                   setTripTableData([adder, tripTableData]);
//                 }
//               },
//               {
//                 500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
//               },
//               {
//                 shipmentId: selectedShipmentId
//               }
//             )
//             resolve();
//           })
//         }),
//         onRowDelete: selectedIds => new Promise((resolve, reject) => {
//           setTimeout(() => {
//             console.log("Old data => ", selectedIds);
//             for (var i = 0; i < selectedIds.length; i++) {
//               request(
//                 "delete",
//                 `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${selectedIds[i]}`,
//                 (res) => {
//                 }
//               );
//             }
//             const deleteTable = tripTableData.filter(
//               trip => !selectedIds.includes(trip["deliveryTripId"]));
//             setTripTableData(deleteTable);
//             successNoti(`Đã xóa ${selectedIds.length} bản ghi`);
//             resolve();
//           })
//         })
//       }}
//       onRowClick={(event, rowData) => {
//         window.location.href = `${path}/${rowData.deliveryTripId}`;
//       }}
//       options={{
//         selection: true,
//         pageSize: 10,
//         search: true,
//         sorting: true,
//       }}
//     />
//   </Fragment>)
// }

// const SCR_ID = "SCR_WMSv2_DELIVERY_TRIP_LISTING";
// export default withScreenSecurity(DeliveryTripListing, SCR_ID, true);

// By Diep
import { TextField } from "@mui/material";
import { request } from "api";
import { ShipmentDropDown } from "components/table/DropDown";
import StandardTable from "components/StandardTable";
import { Fragment, useEffect, useState } from "react"
import { API_PATH, API_PATH_2 } from "screens/apiPaths";
import { getCurrentDateInString2 } from "screens/utils/utils";
import { errorNoti, successNoti, infoNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const DeliveryTripListing = () => {
  const [userLoginId, setUserLoginId] = useState(null);
  const [shipmentList, setShipmentList] = useState([]);
  const [selectedShipmentId, setSelectedShipmentId] = useState(null);
  const [tripTableData, setTripTableData] = useState([]);
  const now = getCurrentDateInString2();
  const [isLoading, setLoading] = useState(true);

  const history = useHistory();
  const { path } = useRouteMatch();
  
  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.GET_USER_LOGIN_ID,
        (res) => {
          setUserLoginId(res.data);
        }
      );

      await request(
        "get",
        API_PATH_2.DELIVERY_MANAGER_SHIPMENT,
        (res) => {
          setShipmentList(res.data);
        }
      );
      
      await request(
        "get",
        API_PATH_2.DELIVERY_MANAGER_DELIVERY_TRIP,
        (res) => {
          setTripTableData(res.data);
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
      rowKey="deliveryTripId"
      title="Danh sách các chuyến giao hàng"
      hideCommandBar={true}
      columns={[
        { title: "Mã chuyến", field: "deliveryTripId",
          editComponent: <TextField InputProps={{readOnly: true}}/> },
        { title: "Ngày tạo", field: "createdStamp",
          editComponent: <TextField value={now} InputProps={{readOnly: true}}/> },
        { title: "Người tạo", field: "createdBy", 
          editComponent: <TextField value={userLoginId} InputProps={{readOnly: true}}/> }, 
        { title: "Đợt giao hàng", field: "shipmentId",
          editComponent: <ShipmentDropDown shipmentList={shipmentList}
            setSelectedShipmentId={setSelectedShipmentId} />},
        { title: "Trạng thái", field: "deliveryTripStatus",
          editComponent: <TextField value={"Khởi tạo"} InputProps={{readOnly: true}}/>}
      ]}
      data={tripTableData}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            if (selectedShipmentId == null) {
              infoNoti('Vui lòng chọn mã đợt giao hàng mong muốn');
              return;
            }
            request(
              "put",
              API_PATH_2.DELIVERY_MANAGER_DELIVERY_TRIP,
              (res) => {
                if (res.status == 200) {
                  const adder = {
                    deliveryTripId: res.data.deliveryTripId,
                    createdStamp: now,
                    createdBy: userLoginId,
                    shipmentId: selectedShipmentId,
                    deliveryTripStatus: "Khởi tạo"
                  };
                  setTripTableData([adder, ...tripTableData]);
                  setSelectedShipmentId(null);
                }
              },
              {
                500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
              },
              {
                shipmentId: selectedShipmentId
              }
            )
            resolve();
          })
        }),
        onRowDelete: selectedIds => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("Old data => ", selectedIds, tripTableData);
            const preDeleteTable = tripTableData.filter(
              trip => selectedIds.includes(trip["deliveryTripId"]));
            let availableSelectedIds = [];
            preDeleteTable.forEach(trip => {
              if (trip.deliveryTripStatusCode == 'CREATED') {
                availableSelectedIds.push(trip.deliveryTripId);
              }
            });
            if (availableSelectedIds.length > 0) {
              for (var i = 0; i < availableSelectedIds.length; i++) {
                request(
                  "delete",
                  `${API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP}/${availableSelectedIds[i]}`,
                  (res) => {
                  }
                );
              } 
              const deleteTable = tripTableData.filter(
                trip => !availableSelectedIds.includes(trip["deliveryTripId"]));
              setTripTableData(deleteTable);
              successNoti(`Đã xóa ${availableSelectedIds.length} bản ghi`);
              resolve();
            } else {
              infoNoti(`Không có bản ghi hợp lệ để xoá`);
              resolve();
            }
          })
        }),
        isDeletingDeliveryTrip: true,
      }}
      onRowClick={(event, rowData) => {
        history.push(`${path}/${rowData.deliveryTripId}`);
      }}
      options={{
        selection: true,
        pageSize: 10,
        search: true,
        sorting: true,
      }}
    />
  </Fragment>)
}

const SCR_ID = "SCR_WMSv2_DELIVERY_TRIP_LISTING";
export default withScreenSecurity(DeliveryTripListing, SCR_ID, true);
