import { KeyboardArrowLeft, KeyboardArrowRight } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Box,
  Button,
  Divider,
  IconButton,
  Stack,
  Typography,
} from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomModal from "components/modal/CustomModal";
import CustomOrderTable from "components/table/CustomOrderTable";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetBillItemOfSaleOrder,
  useGetDeliveryBillList,
} from "controllers/query/bill-query";
import { unix } from "moment";
import { useCallback, useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { Action } from "../../components/action/Action";
import PrimaryButton from "../../components/button/PrimaryButton";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomizedDialogs from "../../components/dialog/CustomizedDialogs";
import CustomDeliveryBillTable from "../../components/table/CustomDeliveryBillTable";
import { useGetFacilityInventory } from "../../controllers/query/facility-query";
import {
  useGetSaleOrderItems,
  useUpdateSaleOrderStatus,
} from "../../controllers/query/sale-order-query";
import { AppColors } from "../../shared/AppColors";
import { ORDERS_STATUS } from "../../shared/AppConstants";
import { deliveryBillCols } from "./LocalConstant";
import CreateDeliveryBill from "./components/CreateDeliveryBill";
function SaleOrderDetailScreen() {
  const location = useLocation();
  const [isApproved, setIsApproved] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [orderApproved, setOrderApprove] = useState(false);
  const currOrder = location.state.order;
  const previous = location?.state?.previous;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  const [showTable1, setShowTable1] = useState(true);

  const toggleTables = () => {
    setShowTable1((prev) => !prev);
  };

  const actions = useMemo(() => {
    return [
      {
        title:
          previous === "saleOrderScreen"
            ? currOrder?.status === ORDERS_STATUS.accepted
              ? "Đã phê duyệt"
              : "Phê duyệt đơn hàng"
            : "Tạo phiếu xuất kho",
        callback: (pre) => {
          if (previous === "saleOrderScreen") {
            setIsApproved((pre) => !pre);
            setItemSelected(pre);
          } else {
            setIsAdd((pre) => !pre);
          }
        },
        icon:
          previous === "saleOrderScreen" ? <CheckCircleIcon /> : <AddIcon />,
        describe: "Thêm bản ghi mới",
        disabled:
          previous === "saleOrderScreen" &&
          currOrder?.status === ORDERS_STATUS.accepted,

        color: orderApproved ? "success" : null,
      },
    ];
  }, [previous, currOrder, orderApproved]);
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
  const updateSaleOrderQuery = useUpdateSaleOrderStatus({
    orderCode: currOrder?.code,
  });
  console.log("Curr: ", currOrder);
  const renderCustomTable = useCallback(() => {
    return (
      <CustomOrderTable
        items={orderItem?.content ? orderItem?.content : []}
        currOrder={currOrder}
      />
    );
  }, [orderItem, currOrder]);
  const renderCustomBill = useCallback(() => {
    return (
      <CustomDeliveryBillTable
        orderItem={orderItem?.content}
        billItem={billItem}
        product_facility={facilityInventory?.content}
      />
    );
  }, [orderItem, billItem, facilityInventory]);

  const handleUpdateOrder = async () => {
    let updateData = {
      status: "accepted",
    };
    if (itemSelected) {
      await updateSaleOrderQuery.mutateAsync(updateData);
      setOrderApprove(true);
    }
    setIsApproved((pre) => !pre);
  };
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          containSearch={false}
          containFilter={false}
        />
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
        <Stack sx={{ flexDirection: "row", marginTop: 2 }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            1. Mã đơn:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currOrder?.code}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            2. Khách hàng:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currOrder?.customer?.name}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            3. Người tạo:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currOrder?.user?.id}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            4. Thời gian tạo:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {unix(currOrder?.createdDate).format("DD-MM-YYYY")}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            5. Kho trực thuộc:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currOrder?.customer?.facility?.name}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            6. Chi tiết
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          ></Typography>
        </Stack>
      </Box>
      <Stack direction={"row"} alignItems={"center"} justifyContent={"center"}>
        {renderCustomTable(currOrder)}
      </Stack>
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
      {previous === "saleOrderScreen" ? null : (
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
      )}
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
      <CustomizedDialogs
        open={isApproved}
        handleClose={setIsApproved}
        contentTopDivider
        contentBottomDivider
        centerTitle="Phê duyệt đơn hàng này?"
        content={
          <Typography color="textSecondary" gutterBottom style={{ padding: 8 }}>
            Bạn có đồng ý phê duyệt đơn hàng đã tạo này?
          </Typography>
        }
        actions={[
          <Button onClick={setIsApproved}>Hủy bỏ</Button>,
          <PrimaryButton onClick={handleUpdateOrder}>Phê duyệt</PrimaryButton>,
        ]}
        customStyles={{
          contents: (theme) => ({ width: "100%" }),
          actions: (theme) => ({ paddingRight: theme.spacing(2) }),
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_SALE_ORDER_DETAIL";
export default withScreenSecurity(SaleOrderDetailScreen, SCR_ID, true);
