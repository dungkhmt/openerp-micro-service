import { TextField } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable"
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { convertTimeStampToDate, getCurrentDateInString } from "screens/utils/utils";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const ShipmentListing = () => {
  const history = useHistory();
  const { path } = useRouteMatch();

  const [shipmentTableData, setShipmentTableData] = useState([]);
  const [numOrder, setNumOrder] = useState(1);
  const [userLoginId, setUserLoginId] = useState(null);
  const now = getCurrentDateInString();
  const [expectDeliveryDate, setExpectDeliveryDate] = useState(null);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {  
      await request(
        "get",
        API_PATH.DELIVERY_MANAGER_SHIPMENT,
        (res) => {
          var data = res.data;
          for (var i = 0; i < data?.length; i++) {
            data[i].numOrder = i + 1;
          }
          setShipmentTableData(res.data);
          setNumOrder(data.length + 1);
        }
      );

      await request(
        "get",
        API_PATH.GET_USER_LOGIN_ID,
        (res) => {
          setUserLoginId(res.data);
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
      hideCommandBar={true}
      rowKey="shipmentId"
      title="Danh sách các đợt giao hàng"
      data={shipmentTableData}
      columns={[
        // { title: "Số thứ tự", field: "numOrder", 
        //   editComponent: <TextField value={numOrder}/> },
        { title: "Mã chuyến", field: "shipmentId", 
          editComponent: <TextField InputProps={{readOnly: true}}/> },
        { title: "Ngày tạo", field: "createdStamp", 
          editComponent: <TextField value={now}/> },
        { title: "Người tạo", field: "createdBy", 
          editComponent: <TextField value={userLoginId}/> }, 
        { title: "Ngày giao hàng dự kiến", field: "expectedDeliveryStr", 
          editComponent: <TextField type="date" value={expectDeliveryDate} 
          onChange={(e) => setExpectDeliveryDate(e.target.value)} /> }
      ]}
      options={{
        selection: false,
        pageSize: 10,
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
                  setShipmentTableData([adder, ...shipmentTableData]);
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
        onRowDelete: selectedIds => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("Selected ids => ", selectedIds);
            request(
              "delete",
              `${API_PATH.DELIVERY_MANAGER_SHIPMENT}/${selectedIds.join(',')}`,
              (res) => {
                successNoti("Xóa bản ghi thành công");
                const dataDelete = shipmentTableData.filter(
                  shipment => !selectedIds.includes(shipment["shipmentId"]));
                setShipmentTableData([...dataDelete]);
              },
              {
                500: () => errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau")
              }
            )
          })
        })
      }}
    />
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_SHIPMENT_LISTING";
export default withScreenSecurity(ShipmentListing, SCR_ID, true);
