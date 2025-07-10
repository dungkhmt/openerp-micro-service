import { TextField } from "@mui/material";
import { request } from "api";
import StandardTable from "components/StandardTable"
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import { Fragment, useEffect, useState } from "react"
import { API_PATH, API_PATH_2 } from "screens/apiPaths";
import { errorNoti, successNoti } from "utils/notification";

const DeliveryPersonManagement = () => {
  const [deliveryPersonsTableData, setDeliveryPersonsTableData] = useState([]);

  const [newFullName, setNewFullName] = useState(null);
  const [newPhoneNumber, setNewPhoneNumber] = useState(null);
  const [newUserLoginId, setNewUserLoginId] = useState(null);
  const [isLoading, setLoading] = useState(true);
  
  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH_2.DELIVERY_MANAGER_DELIVERY_PERSON,
        (res) => {
          setDeliveryPersonsTableData(res.data);
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
      title="Quản lý nhân viên giao hàng"
      columns={[
        { title: "Tên *", field: "fullName", 
          editComponent: <TextField onChange={(e) => setNewFullName(e.target.value)} /> },
        { title: "Số điện thoại", field: "phoneNumber",
          editComponent: <TextField onChange={(e) => setNewPhoneNumber(e.target.value)} /> },
        { title: "Tài khoản đăng nhập *", field: "userLoginId",
          editComponent: <TextField onChange={(e) => setNewUserLoginId(e.target.value)} /> }
      ]}
      data={deliveryPersonsTableData}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
      rowKey={"userLoginId"}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("New data => ", newData);
            if (newFullName == null || newFullName == undefined ||
              newUserLoginId == null || newUserLoginId == undefined) {
              errorNoti("Vui lòng điền đầy đủ thông tin người giao hàng");
              reject();
            } else {
              request(
                "put",
                API_PATH.DELIVERY_MANAGER_DELIVERY_PERSON,
                (res) => {
                  if (res.status == 200) {
                    const newTableData = res.data;
                    successNoti("Thêm mới nhân viên thành công");
                    setDeliveryPersonsTableData([...deliveryPersonsTableData, newTableData]);
                    resolve();
                  }
                },
                {
                  500: () => errorNoti("Có lỗi xảy ra khi thêm mới nhân viên")
                },
                {
                  "fullName": newFullName,
                  "phoneNumber": newPhoneNumber,
                  "userLoginId": newUserLoginId
                }
              );
            }
          });
        }),
        onRowDelete: selectedIds => new Promise((resolve, reject) => {
          setTimeout(() => {
            request(
              "delete",
              `${API_PATH.DELIVERY_MANAGER_DELIVERY_PERSON}/${selectedIds.join(',')}`,
              (res) => {
                if (res.status == 200) {
                  successNoti("Xóa nhân viên thành công");
                  resolve();
                }
              },
              {
                500: () => errorNoti("Có lỗi xảy ra khi thêm mới nhân viên")
              }
            )
          })
        })
      }}
    />
  </Fragment>);
};

const SCR_ID = "SCR_WMSv2_DELIVERY_PERSON_MANAGEMENT";
export default withScreenSecurity(DeliveryPersonManagement, SCR_ID, true);
