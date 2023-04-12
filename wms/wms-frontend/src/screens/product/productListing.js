import { useRouteMatch } from "react-router-dom";
import { useHistory } from "react-router";
import { request } from "api";
import StandardTable from "components/table/StandardTable";
import { API_PATH } from "../apiPaths";
import CommandBarButton from "components/button/commandBarButton";
import { Link } from 'react-router-dom';
import { successNoti } from "utils/notification";
import { Fragment, useState, useEffect } from "react";
import AddIcon from '@mui/icons-material/Add';

const ProductListing =  () => {
  const [productTableData, setProductTableData] = useState([]);
  const [isHideCommandBar, setHideCommandBar] = useState(true);
  const history = useHistory();
  const { path } = useRouteMatch();
  
  const columns = [
    { title: "Tên sản phẩm", field: "name" },
    { title: "Mã sản phẩm", field: "code" },
    { title: "Số lượng hàng tồn", field: "onHandQuantity" }
  ];

  const onSelectionChangeHandle = (rows) => {
    setProductTableData(productTableData);
    if (rows.length === 0) {
      setHideCommandBar(true);
    } else {
      setHideCommandBar(false);
    }
  }

  const removeSelectedProducts = () => {
    const selectedProductIds = productTableData
      .filter((product) => product.tableData.checked == true)
      .map((obj) => obj.productId);
    
    console.log("selected product ids: ", selectedProductIds);

    request(
      "delete",
      API_PATH.PRODUCT,
      (res) => { 
        successNoti("Xóa thành công");
        const newTableData = productTableData.filter(
          (product) => !selectedProductIds.includes(product.productId));
        setProductTableData(newTableData);
        setHideCommandBar(true);
      },
      { },
      selectedProductIds
    )
  }

  useEffect(() => {
    async function fetchData() {
      request(
        "get",
        API_PATH.PRODUCT,
        (res) => {
          console.log("data response -> ", res);
          setProductTableData(res.data);
          console.log("product table data -> ", productTableData);
        }
      )
    }

    fetchData();
  }, []);

  return (
    <Fragment>
      <StandardTable
        title={"Danh sách sản phẩm"}
        columns={columns}
        data={productTableData}
        hideCommandBar={isHideCommandBar}
        options={{
          selection: true,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
        onRowClick={ (event, rowData) => {
          window.location.href = `${path}/update/${rowData.productId}`;
        } } 
        onSelectionChange={onSelectionChangeHandle}
        commandBarComponents={ <CommandBarButton 
          onClick={removeSelectedProducts}>
            Xóa
        </CommandBarButton> }
        actions={[
          {
            icon: () => <Link to={`product/create`}>
              <AddIcon />
            </Link>,
            tooltip: "Thêm mới sản phẩm",
            isFreeAction: true
          }
        ]}
      />
    </Fragment>
  );
}

export default ProductListing;