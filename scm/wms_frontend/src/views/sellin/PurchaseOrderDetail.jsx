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
import { Action } from "components/action/Action";
import PrimaryButton from "components/button/PrimaryButton";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import CustomModal from "components/modal/CustomModal";
import CustomBillTable from "components/table/CustomBillTable";
import CustomOrderTable from "components/table/CustomOrderTable";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetBillItemOfPurchaseOrder,
  useGetReceiptBillList,
} from "controllers/query/bill-query";
import {
  useGetPurchaseOrderItems,
  useUpdatePurchaseOrderStatus,
} from "controllers/query/purchase-order-query";
import { unix } from "moment";
import { useCallback, useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import { receiptBillCols } from "./LocalConstant";
import CreatePurchaseBill from "./components/CreatePurchaseBill";

function PurchaseOrderDetailScreen({}) {
  const location = useLocation();
  const [isApproved, setIsApproved] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [orderApproved, setOrderApprove] = useState(false);
  const currOrder = location.state.order;
  const previous = location?.state?.previous;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
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
          previous === "purchaseOrderScreen"
            ? currOrder?.status === ORDERS_STATUS.accepted
              ? "Đã phê duyệt"
              : "Phê duyệt đơn hàng"
            : "Tạo phiếu nhập kho",
        callback: (pre) => {
          if (previous === "purchaseOrderScreen") {
            setIsApproved((pre) => !pre);
            setItemSelected(pre);
          } else {
            setIsAdd((pre) => !pre);
          }
        },
        icon:
          previous === "purchaseOrderScreen" ? (
            <CheckCircleIcon />
          ) : (
            <AddIcon />
          ),
        describe: "Thêm bản ghi mới",
        disabled:
          previous === "purchaseOrderScreen" &&
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
  const { isLoading, data: orderItem } = useGetPurchaseOrderItems({
    orderCode: currOrder?.code,
  });
  const { isLoading: isLoadingBillItem, data: billItem } =
    useGetBillItemOfPurchaseOrder({
      orderCode: currOrder?.code,
    });

  const { isLoading: isLoadingBill, data: bills } = useGetReceiptBillList({
    orderCode: currOrder?.code,
  });
  const updatePurchaseOrderQuery = useUpdatePurchaseOrderStatus({
    orderCode: currOrder?.code,
  });
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
      <CustomBillTable
        orderItem={orderItem?.content}
        billItem={billItem}
        currOrder={currOrder}
      />
    );
  }, [orderItem, billItem, currOrder]);
  const renderReceiptBills = useCallback(() => {
    return (
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoadingBill}
        totalItem={100}
        columns={[
          ...receiptBillCols,
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
    );
  }, [bills]);
  const handleUpdateStatusOrder = async () => {
    let updateData = {
      status: "accepted",
    };
    if (itemSelected) {
      await updatePurchaseOrderQuery.mutateAsync(updateData);
      setOrderApprove(true);
    }

    setIsApproved((pre) => !pre);
  };
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
        <Typography>2. Nhà sản xuất: {currOrder?.supplierCode}</Typography>
        <Typography>3. Người tạo: {currOrder?.user?.id}</Typography>
        <Typography>
          4. Thời gian tạo: {unix(currOrder?.createdDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>5. Kho trực thuộc: {currOrder?.facility?.name}</Typography>
        <Typography sx={{ marginBottom: 2 }}>6. Chi tiết</Typography>
      </Box>
      <Stack direction={"row"} alignItems={"center"} justifyContent={"center"}>
        {renderCustomTable(currOrder)}
      </Stack>
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
      {previous === "purchaseOrderScreen" ? null : (
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
              THÔNG TIN PHIẾU NHẬP ĐƠN HÀNG
            </Typography>
          </Stack>
          {renderReceiptBills()}
        </Box>
      )}
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Phiếu nhập kho"
      >
        {renderCustomBill()}
        <IconButton onClick={toggleTables}>
          {showTable1 ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
        </IconButton>
        <CreatePurchaseBill currOrder={currOrder} setIsAdd={setIsAdd} />
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
          <PrimaryButton onClick={handleUpdateStatusOrder}>
            Phê duyệt
          </PrimaryButton>,
        ]}
        customStyles={{
          contents: (theme) => ({ width: "100%" }),
          actions: (theme) => ({ paddingRight: theme.spacing(2) }),
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_PURCHASE_ORDER_DETAIL";
export default withScreenSecurity(PurchaseOrderDetailScreen, SCR_ID, true);
