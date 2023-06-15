import { request } from "api";
import StandardTable from "components/StandardTable";
import LoadingScreen from "components/common/loading/loading";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "screens/apiPaths";

const DeliveryTripItemsListing = () => {
  
  const [tableData, setTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.DELIVERY_MANAGER_ASSIGN_ORDER_ITEM,
        (res) => {
          setTableData(res.data);
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
      title='Danh sách sản phẩm cần giao'
      hideCommandBar={true}
      columns={[
        { title: "Sản phẩm", field: "productName" },
        { title: "Số lượng", field: "quantity" },
        { title: "Kệ hàng", field: "bayCode" },
        { title: "Lô", field: "lotId" },
        { title: "Kho", field: "warehouseName" },
        { title: "Địa chỉ nhận hàng", field: "customerAddressName" }
      ]}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
      data={tableData}
    />
  </Fragment>);
};


export default DeliveryTripItemsListing;