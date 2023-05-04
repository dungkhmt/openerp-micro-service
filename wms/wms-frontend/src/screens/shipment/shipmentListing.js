import { TextField } from "@mui/material";
import { request } from "api";
import StandardTable from "components/table/StandardTable"
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { convertTimeStampToDate, getCurrentDateInString } from "screens/utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";

const ShipmentListing = () => {
  const history = useHistory();
  const { path } = useRouteMatch();

  const [shipmentTableData, setShipmentTableData] = useState([]);
  const [numOrder, setNumOrder] = useState(1);
  const [userLoginId, setUserLoginId] = useState(null);
  const now = getCurrentDateInString();
  const [expectDeliveryDate, setExpectDeliveryDate] = useState(null);

  useEffect(() => {
    request(
      "get",
      API_PATH.DELIVERY_MANAGER_SHIPMENT,
      (res) => {
        var data = res.data;
        for (var i = 0; i < data?.length; i++) {
          data[i].numOrder = i + 1;
          data[i].expectedDeliveryStamp = convertTimeStampToDate(data[i].expectedDeliveryStamp);
        }
        setShipmentTableData(res.data);
        setNumOrder(data.length + 1);
      }
    );

    request(
      "get",
      API_PATH.GET_USER_LOGIN_ID,
      (res) => {
        setUserLoginId(res.data);
      }
    )
  }, []);

  return <Fragment>
    <StandardTable
      hideCommandBar={true}
      title="Danh sách chuyến giao hàng"
      data={shipmentTableData}
      columns={[
        { title: "Số thứ tự", field: "numOrder", editComponent: props => <TextField value={numOrder}/> },
        { title: "Mã chuyến", field: "shipmentId", editComponent: props => <TextField InputProps={{readOnly: true}}/> },
        { title: "Ngày tạo", field: "createdStamp", editComponent: props => <TextField value={now}/> },
        { title: "Người tạo", field: "createdBy", editComponent: props => <TextField value={userLoginId}/> }, 
        { title: "Ngày giao hàng dự kiến", field: "expectedDeliveryStamp", 
          editComponent: props => <TextField type="date" value={expectDeliveryDate} 
          onChange={(e) => setExpectDeliveryDate(e.target.value)} /> }
      ]}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
      onRowClick={(event, rowData) => {
        window.location.href = `${path}/${rowData.shipmentId}`;
      }}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            request(
              "put",
              API_PATH.DELIVERY_MANAGER_SHIPMENT,
              (res) => {
                if (res.status == 200) {
                  successNoti("Thêm mới đợt giao hàng thành công");
                  const adder = {
                    numOrder: numOrder,
                    createdStamp: now,
                    createdBy: userLoginId,
                    expectedDeliveryStamp: convertTimeStampToDate(expectDeliveryDate),
                    shipmentId: res.data
                  };
                  console.log("Adder => ", adder);
                  setShipmentTableData([...shipmentTableData, adder]);
                  resolve();
                }
              },
              {
                500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
              },
              {
                expectedDeliveryStamp: expectDeliveryDate
              }
            )
          });
        }),
        onRowDelete: oldData => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("Old data => ", oldData);
          })
        })
      }}
    />
  </Fragment>
}

export default ShipmentListing;