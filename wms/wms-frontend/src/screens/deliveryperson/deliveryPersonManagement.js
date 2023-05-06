import { request } from "api";
import StandardTable from "components/table/StandardTable"
import { Fragment, useEffect, useState } from "react"
import { API_PATH } from "screens/apiPaths";
import { errorNoti, successNoti } from "utils/notification";

const DeliveryPersonManagement = () => {
  const [deliveryPersonsTableData, setDeliveryPersonsTableData] = useState([]);
  
  useEffect(() => {
    request(
      "get",
      API_PATH.DELIVERY_MANAGER_DELIVERY_PERSON,
      (res) => {
        setDeliveryPersonsTableData(res.data);
      }
    )
  }, []);

  return <Fragment>
    <StandardTable 
      hideCommandBar={true}
      title="Quản lý nhân viên giao hàng"
      columns={[
        { title: "Tên *", field: "fullName" },
        { title: "Số điện thoại", field: "phoneNumber" },
        { title: "Tài khoản đăng nhập *", field: "userLoginId" }
      ]}
      data={deliveryPersonsTableData}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
      editable={{
        onRowAdd: newData => new Promise((resolve, reject) => {
          setTimeout(() => {
            console.log("New data => ", newData);
            if (newData.fullName == null || newData.fullName == undefined ||
              newData.userLoginId == null || newData.userLoginId == undefined) {
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
                  "fullName": newData.fullName,
                  "phoneNumber": newData.phoneNumber,
                  "userLoginId": newData.userLoginId
                }
              );
            }
          });
        }),
        onRowDelete: oldData => new Promise((resolve, reject) => {
          setTimeout(() => {
            request(
              "delete",
              `${API_PATH.DELIVERY_MANAGER_DELIVERY_PERSON}/${oldData.deliveryPersonId}`,
              (res) => {
                if (res.status == 200) {
                  successNoti("Xóa nhân viên thành công");
                  const dataDelete = [...deliveryPersonsTableData];
                  const index = oldData.tableData.id;
                  dataDelete.splice(index, 1);
                  setDeliveryPersonsTableData([...dataDelete]);
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
  </Fragment>
};

export default DeliveryPersonManagement;