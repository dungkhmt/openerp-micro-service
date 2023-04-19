import { Box } from "@mui/material";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { useWindowSize } from "react-use";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import { useGetProductCateList } from "../../../controllers/query/category-query";
function ProductCategoryScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const { isLoading, data } = useGetProductCateList();
  const [keyword, setKeyword] = useState("");
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 56 - 64 - 200 - 8 }}
        isLoading={isLoading}
        totalItem={100}
        columns={[
          {
            field: "code",
            headerName: "Mã code",
            sortable: false,
            pinnable: true,
          },
          {
            field: "name",
            headerName: "Tên danh mục sản phẩm",
            sortable: false,
            minWidth: 200,
          },
        ]}
        rows={data ? data?.content : []}
      />
    </Box>
  );
}

const SCR_ID = "SCR_PRODUCT_CATEGORY";
export default withScreenSecurity(ProductCategoryScreen, SCR_ID, true);
