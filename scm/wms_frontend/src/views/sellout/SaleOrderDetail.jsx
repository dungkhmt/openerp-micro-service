import { KeyboardArrowLeft, KeyboardArrowRight } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Divider, IconButton, Stack, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomModal from "components/modal/CustomModal";
import CustomOrderTable from "components/table/CustomOrderTable";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateDeliveryBill,
  useGetBillItemOfSaleOrder,
  useGetDeliveryBillList,
} from "controllers/query/bill-query";
import { useGetProductList } from "controllers/query/category-query";
import { unix } from "moment";
import { useCallback, useState } from "react";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { Action } from "../../components/action/Action";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomDeliveryBillTable from "../../components/table/CustomDeliveryBillTable";
import { useGetFacilityInventory } from "../../controllers/query/facility-query";
import { useGetSaleOrderItems } from "../../controllers/query/sale-order-query";
import { AppColors } from "../../shared/AppColors";
import { deliveryBillCols } from "./LocalConstant";
import CreateDeliveryBill from "./components/CreateDeliveryBill";

function SaleOrderDetailScreen() {
  const location = useLocation();
  const currOrder = location.state.order;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const createBillQuery = useCreateDeliveryBill();
  const [isAdd, setIsAdd] = useToggle(false);
  const [showTable1, setShowTable1] = useState(true);

  const toggleTables = () => {
    setShowTable1((prev) => !prev);
  };
  let actions = [
    {
      title: "Tạo phiếu xuất kho",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Xem chi tiết",
      callback: (item) => {},
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
  ];
  const { isLoading, data: orderItem } = useGetSaleOrderItems({
    orderCode: currOrder?.code,
  });
  const { isLoading: isLoadingBillItem, data: billItem } =
    useGetBillItemOfSaleOrder({
      orderCode: currOrder?.code,
    });
  const { isLoading: isLoadingInventory, data: facilityInventory } =
    useGetFacilityInventory({
      code: currOrder?.customer?.facility?.code,
    });
  const { isLoading: isLoadingBill, data: bills } = useGetDeliveryBillList({
    orderCode: currOrder?.code,
  });
  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const renderCustomTable = useCallback(() => {
    return (
      <CustomOrderTable
        items={orderItem?.content ? orderItem?.content : []}
        currOrder={currOrder}
      />
    );
  }, [orderItem]);
  const renderCustomBill = useCallback(() => {
    return (
      <CustomDeliveryBillTable
        orderItem={orderItem?.content}
        billItem={billItem}
        product_facility={facilityInventory?.content}
      />
    );
  }, [orderItem, billItem, facilityInventory]);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} containSearch={false} />
      </Box>
      <Box>
        <Stack direction={"row"} spacing={2} alignItems={"center"}>
          <Stack
            sx={{
              borderRadius: 50,
              background: "gray",
              width: 30,
              height: 30,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography sx={{ color: "white" }}>1</Typography>
          </Stack>
          <Typography
            sx={{
              color: AppColors.secondary,
              fontSize: 18,
              fontWeight: "600",
            }}
          >
            THÔNG TIN ĐƠN HÀNG
          </Typography>
        </Stack>
        <Typography
          sx={{
            fontSize: 16,
            marginTop: 2,
          }}
        >
          1. Mã đơn: {currOrder?.code}
        </Typography>
        <Typography>2. Khách hàng: {currOrder?.customer?.name}</Typography>
        <Typography>3. Người tạo: {currOrder?.user?.id}</Typography>
        <Typography>
          4. Thời gian tạo: {unix(currOrder?.createdDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>
          5. Kho trực thuộc: {currOrder?.customer?.facility?.name}
        </Typography>
        <Typography sx={{ marginBottom: 2 }}>6. Chi tiết</Typography>
      </Box>
      <Stack direction={"row"} alignItems={"center"} justifyContent={"center"}>
        {renderCustomTable(currOrder)}
      </Stack>
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
      <Box sx={{ marginY: 2 }}>
        <Stack direction={"row"} spacing={2} alignItems={"center"}>
          <Stack
            sx={{
              borderRadius: 50,
              background: "gray",
              width: 30,
              height: 30,
              alignItems: "center",
              justifyContent: "center",
              marginY: 2,
            }}
          >
            <Typography sx={{ color: "white" }}>2</Typography>
          </Stack>
          <Typography
            sx={{
              color: AppColors.secondary,
              fontSize: 18,
              fontWeight: "600",
            }}
          >
            THÔNG TIN PHIẾU XUẤT ĐƠN HÀNG
          </Typography>
        </Stack>
        <CustomDataGrid
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
          isLoading={isLoadingBill}
          totalItem={100}
          columns={[
            ...deliveryBillCols,
            {
              field: "action",
              headerName: "Hành động",
              headerAlign: "center",
              align: "center",
              sortable: false,
              flex: 1,
              type: "actions",
              getActions: (params) => {
                return [
                  ...extraActions.map((extraAction, index) => (
                    <Action
                      item={params.row}
                      key={index}
                      extraAction={extraAction}
                      onActionCall={extraAction.callback}
                    />
                  )),
                ];
              },
            },
          ]}
          rows={bills ? bills?.content : []}
        />
      </Box>
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Phiếu xuất kho"
      >
        {renderCustomBill()}
        <IconButton onClick={toggleTables}>
          {showTable1 ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
        </IconButton>
        <CreateDeliveryBill currOrder={currOrder} setIsAdd={setIsAdd} />
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_PURCHASE_ORDER_DETAIL";
export default withScreenSecurity(SaleOrderDetailScreen, SCR_ID, true);
