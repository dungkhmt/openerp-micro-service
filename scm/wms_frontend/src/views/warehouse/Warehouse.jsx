import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomDrawer from "../../components/drawer/CustomDrawer";
import { useGetProductList } from "../../controllers/query/category-query";
import { useGetFacilityList } from "../../controllers/query/facility-query";
import { Action } from "../sellin/PurchaseOrder";
function WarehouseScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const { isLoading, data } = useGetFacilityList();
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        // setItemSelected(item);
        // setIsEdit();
      },
      icon: <EditIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Xóa",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
      },
      icon: <DeleteIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
    {
      title: "Xem",
      callback: (item) => {
        setOpenDrawer((pre) => !pre);
      },
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];
  var product_fields = [
    {
      field: "code",
      headerName: "Mã code",
      sortable: false,
      pinnable: true,
    },
    {
      field: "name",
      headerName: "Tên sản phẩm",
      sortable: false,
      minWidth: 200,
    },
    {
      field: "status",
      headerName: "Trạng thái",
      sortable: false,
      minWidth: 100,
    },
    {
      field: "quantity",
      headerName: "Số lượng mua",
      sortable: false,
      minWidth: 150,
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
          color={green[800]}
          fontSize={17}
        >
          {"KHO HÀNG"}
        </Typography>
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
            minWidth: 150,
          },
          {
            field: "name",
            headerName: "Tên kho",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "createdBy",
            headerName: "Người tạo",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.creator?.id;
            },
          },
          {
            field: "address",
            headerName: "Địa chỉ",
            sortable: false,
            pinnable: true,
          },
          {
            field: "managedBy",
            headerName: "Thủ kho",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.manager?.name
                ? params?.row?.manager?.name
                : "Chưa có";
            },
          },
          {
            field: "status",
            headerName: "Trạng thái",
            sortable: false,
            minWidth: 150,
            renderCell: (params) => {
              return (
                <Button variant="outlined" color="info">
                  {params?.row?.status}
                </Button>
              );
            },
          },
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  disabled={false}
                />
              )),
            ],
            // renderCell: (params) => (
            //   <Action
            //     disabled={false}
            //     extraAction={extraActions[0]}
            //     item={params.row}
            //   />
            // ),
          },
        ]}
        rows={data ? data?.content : []}
      />
      <CustomDrawer
        open={isOpenDrawer}
        onClose={setOpenDrawer}
        // style={{ zIndex: 1000 }}
      >
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
            {"TỒN KHO"}
          </Typography>
        </Box>
        <CustomDataGrid
          isSelectable={true}
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
          isLoading={isLoadingProduct}
          totalItem={100}
          columns={product_fields}
          rows={product ? product?.content : []}
        />
      </CustomDrawer>
    </Box>
  );
}

const SCR_ID = "SCR_WAREHOUSE";
export default withScreenSecurity(WarehouseScreen, SCR_ID, true);
