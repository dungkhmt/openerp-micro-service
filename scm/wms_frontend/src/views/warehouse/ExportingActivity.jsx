import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import { useGetDeliveryBillList } from "../../controllers/query/bill-query";
import { useGetSaleOrderList } from "../../controllers/query/sale-order-query";
import { AppColors } from "../../shared/AppColors";
import { Action } from "../sellin/PurchaseOrder";
import { staticSaleOrderCols } from "./LocalConstant";
function ExportingActivityScreen({ screenAuthorization }) {
  let { path } = useRouteMatch();
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const history = useHistory();
  const { height } = useWindowSize();

  const { isLoading, data } = useGetSaleOrderList({
    orderStatus: "accepted",
  });
  const { isLoading: isLoadingReceiptBill, data: deliveryBills } =
    useGetDeliveryBillList({
      orderStatus: "accepted",
    });

  const handleButtonClick = (params) => {
    console.log("params: ", params);
    history.push(`${path}/sale-order-detail`, {
      order: params,
    });
  };

  let actions = [
    {
      title: "Xuất kho",
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
  const extraActions = [
    {
      title: "Xem chi tiết",
      callback: (item) => {
        console.log("item: ", item);
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
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
          {"TÌNH HÌNH XUẤT KHO"}
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
          ...staticSaleOrderCols,
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            renderCell: (params) => (
              <Action
                disabled={false}
                extraAction={extraActions[0]}
                item={params.row}
                onActionCall={extraActions[0].callback}
              />
            ),
          },
        ]}
        rows={data ? data?.content : []}
      />
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
            field: "createdDate",
            headerName: "Thời điểm tạo",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "status",
            headerName: "Trạng thái",
            sortable: false,
            minWidth: 150,
            renderCell: (params) => {
              return (
                <Button variant="outlined" color="info">
                  {"IN PROGRESS"}
                </Button>
              );
            },
          },
          // {
          //   field: "facility",
          //   headerName: "Kho trực thuộc",
          //   sortable: false,
          //   minWidth: 150,
          //   valueGetter: (params) => {
          //     return params.row.facility.name;
          //   },
          // },
          {
            field: "order",
            headerName: "Mã đơn hàng",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.saleOrder.code;
            },
          },
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            renderCell: (params) => (
              <Action
                disabled={false}
                extraAction={extraActions[0]}
                item={params.row}
                onActionCall={extraActions[0].callback}
              />
            ),
          },
        ]}
        rows={deliveryBills ? deliveryBills?.content : []}
      />
    </Box>
  );
}

const SCR_ID = "SCR_EXPORTING_ACTIVITY";
export default withScreenSecurity(ExportingActivityScreen, SCR_ID, true);
