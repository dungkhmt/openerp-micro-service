import { TextField } from "@mui/material";
import { request } from "api";
import { ShipmentDropDown } from "components/table/DropDown";
import StandardTable from "components/table/StandardTable";
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { getCurrentDateInString } from "screens/utils/utils";
import { errorNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";

const DeliveryTripListing = () => {
  const [userLoginId, setUserLoginId] = useState(null);
  const [shipmentList, setShipmentList] = useState([]);
  const [selectedShipmentId, setSelectedShipmentId] = useState(null);
  const [tripTableData, setTripTableData] = useState([]);
  const now = getCurrentDateInString();

  const history = useHistory();
  const { path } = useRouteMatch();
  
  useEffect(() => {
    request(
      "get",
      API_PATH.GET_USER_LOGIN_ID,
      (res) => {
        setUserLoginId(res.data);
      }
    );

    request(
      "get",
      API_PATH.DELIVERY_MANAGER_SHIPMENT,
      (res) => {
        setShipmentList(res.data);
      }
    );
    
    request(
      "get",
      API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
      (res) => {
        setTripTableData(res.data);
      }
    )
  }, []);

  return <Fragment>
    <StandardTable
      title="Danh sách các chuyến giao hàng"
      hideCommandBar={true}
      columns={[
        { title: "Mã chuyến", field: "deliveryTripId",
          editComponent: props => <TextField InputProps={{readOnly: true}}/> },
        { title: "Ngày tạo", field: "createdStamp",
          editComponent: props => <TextField value={now}/> },
        { title: "Người tạo", field: "createdBy", 
          editComponent: props => <TextField value={userLoginId}/> }, 
        { title: "Đợt giao hàng", field: "shipmentId",
          editComponent: props => <ShipmentDropDown shipmentList={shipmentList}
            setSelectedShipmentId={setSelectedShipmentId} />}
      ]}
      data={tripTableData}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            request(
              "put",
              API_PATH.DELIVERY_MANAGER_DELIVERY_TRIP,
              (res) => {
                if (res.status == 200) {
                  const adder = {
                    deliveryTripId: res.data.deliveryTripId,
                    createdStamp: now,
                    createdBy: userLoginId,
                    shipmentId: selectedShipmentId
                  };
                  setTripTableData([...tripTableData, adder]);
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
        onRowDelete: oldData => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("Old data => ", oldData);
            resolve();
          })
        })
      }}
      onRowClick={(event, rowData) => {
        window.location.href = `${path}/${rowData.deliveryTripId}`;
      }}
      options={{
        selection: false,
        pageSize: 10,
        search: true,
        sorting: true,
      }}
    />
  </Fragment>
}

export default DeliveryTripListing;