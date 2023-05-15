import { useRouteMatch } from "react-router-dom";
import { useHistory } from "react-router";
import { request } from "api";
import StandardTable from "components/StandardTable";
import { API_PATH } from "../apiPaths";
import { Link } from 'react-router-dom';
import { successNoti } from "utils/notification";
import { Fragment, useState, useEffect } from "react";
import AddIcon from '@mui/icons-material/Add';
import LoadingScreen from "components/common/loading/loading";

const ProductListing =  () => {
  const [productTableData, setProductTableData] = useState([]);
  const { path } = useRouteMatch();
  const [isLoading, setLoading] = useState(true);
  
  const columns = [
    { title: "Tên sản phẩm", field: "name" },
    { title: "Mã sản phẩm", field: "code" },
    { title: "Số lượng hàng tồn", field: "onHandQuantity" }
  ];

  useEffect(() => {
    async function fetchData() {
      await request(
        "get",
        API_PATH.PRODUCT_WITHOUT_IMAGE,
        (res) => {
          setProductTableData(res.data);
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
        rowKey="productId"
        title={"Danh sách sản phẩm"}
        columns={columns}
        data={productTableData}
        options={{
          selection: true,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
        onRowClick={ (event, rowData) => {
          window.location.href = `${path}/update/${rowData.productId}`;
        } } 
        actions={[
          {
            icon: <Link to={`product/create`}>
              <AddIcon />
            </Link>,
            tooltip: "Thêm mới sản phẩm",
            isFreeAction: true
          }
        ]}
        editable={{
          onRowDelete: (selectedIds) => new Promise((resolve, reject) => {
            setTimeout(() => {
              request(
                "delete",
                API_PATH.PRODUCT,
                (res) => { 
                  const deleteData = productTableData.filter(
                    product => !selectedIds.includes(product["productId"])
                  );
                  setProductTableData(deleteData);
                  successNoti(`Đã xóa ${selectedIds.length} bản ghi`);
                },
                { },
                selectedIds
              )
            })
          })
        }}
      />
    </Fragment>
  );
}

export default ProductListing;