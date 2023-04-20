import { Box, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
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
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          boxShadow: 3,
          margin: "0px -16px 0 -16px",
          paddingX: 2,
          paddingY: 1,
          position: "sticky",
          backgroundColor: "white",
          zIndex: 1000,
        }}
      >
        <Typography
          id="modal-modal-title"
          variant="h6"
          textTransform="capitalize"
          letterSpacing={1}
          color={green[800]}
          fontSize={17}
        >
          {"DANH MỤC SẢN PHẨM"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
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
          {
            field: "",
            headerName: "Hành động",
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
