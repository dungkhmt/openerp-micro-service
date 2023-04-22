import AddIcon from "@mui/icons-material/Add";
import { Box, Typography } from "@mui/material";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import { useGetCustomerList } from "../../controllers/query/category-query";
import { AppColors } from "../../shared/AppColors";
function ImportingActivityScreen({ screenAuthorization }) {
  let { path } = useRouteMatch();
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const history = useHistory();

  const handleButtonClick = () => {
    history.push("/sellin/purchase-order");
  };

  const { height } = useWindowSize();
  const { isLoading, data } = useGetCustomerList();
  let actions = [
    {
      title: "Nhập kho",
      callback: (pre) => {
        handleButtonClick();
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
    {
      title: "Sửa",
      callback: () => {
        console.log("call back");
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
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
          fontSize={18}
          sx={{
            fontFamily: "Open Sans",
            color: AppColors.primary,
            fontWeight: "bold",
          }}
        >
          {"TÌNH HÌNH NHẬP KHO"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar actions={actions} />
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
            headerName: "Tên khách hàng",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "phone",
            headerName: "Số điện thoại",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "address",
            headerName: "Địa chỉ",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "status",
            headerName: "Trạng thái",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "customerType",
            headerName: "Loại khách hàng",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.customerType.name;
            },
          },
          {
            field: "contractType",
            headerName: "Loại hợp đồng",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.contractType.name;
            },
          },
          {
            field: "createdBy",
            headerName: "Mã người tạo",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.user.id;
            },
          },
          {
            field: "",
            headerName: "Hành động",
            sortable: false,
            minWidth: 150,
          },
        ]}
        rows={data ? data?.content : []}
      />
    </Box>
  );
}

const SCR_ID = "SCR_IMPORTING_ACTIVITY";
export default withScreenSecurity(ImportingActivityScreen, SCR_ID, true);
