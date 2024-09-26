import { KeyboardArrowLeft, KeyboardArrowRight } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PictureAsPdfIcon from "@mui/icons-material/PictureAsPdf";
import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Box,
  Button,
  Divider,
  IconButton,
  Stack,
  Typography,
} from "@mui/material";
import axios from "axios";
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
import { useCallback, useLayoutEffect, useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import { endPoint } from "../../controllers/endpoint";
import { convertUserToName } from "../../utils/GlobalUtils";
import { receiptBillCols } from "./LocalConstant";
import BillDetailModal from "./components/BillDetailModal";
import CreatePurchaseBill from "./components/CreatePurchaseBill";

function PurchaseOrderDetailScreen() {
  const location = useLocation();
  const [isApproved, setIsApproved] = useToggle(false);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isSeeBillDetail, setSeeBillDetail] = useToggle(false);
  const [itemSelected, setItemSelected] = useState();
  const [billSelected, setBillSelected] = useState();
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
  const actions = useMemo(() => {
    return [
      {
        title:
          previous === "purchaseOrderScreen"
            ? currOrder?.status === ORDERS_STATUS.accepted ||
              currOrder?.status === ORDERS_STATUS.delivering
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
          (previous === "purchaseOrderScreen" &&
            (currOrder?.status === ORDERS_STATUS.accepted ||
              currOrder?.status === ORDERS_STATUS.delivering)) ||
          (previous !== "purchaseOrderScreen" &&
            currOrder?.status === ORDERS_STATUS.delivering),

        color: orderApproved ? "success" : null,
      },
      {
        title: "Xuất pdf",
        callback: async () => {
          let params = {
            orderCode: currOrder?.code,
          };
          const axiosConfig = {
            responseType: "arraybuffer",
            headers: {
              Accept: "application/json",
            },
          };
          const queryString = new URLSearchParams(params).toString();
          axios
            .get(
              endPoint.exportPurchaseOrderPdf + "?" + queryString,
              axiosConfig
            )
            .then((res) => {
              const fileURL = window.URL.createObjectURL(new Blob([res.data]));
              // Setting various property values
              let alink = document.createElement("a");
              alink.setAttribute(
                "download",
                `purchase_order_${currOrder?.code}.pdf`
              );
              document.body.appendChild(alink);
              alink.href = fileURL;
              alink.click();
            });
        },
        icon: <PictureAsPdfIcon />,
        describe: "Xuất báo cáo pdf",
        disabled: false,
      },
    ];
  }, [previous, currOrder, orderApproved]);

  const extraActions = [
    {
      title: "Xem chi tiết",
      callback: (item) => {
        if (isLoadingBillItem) {
          return;
        }
        let billItemsOfBill = billItem?.filter(
          (bill) => bill?.receiptBill?.code === item?.code
        );
        setBillSelected(billItemsOfBill);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
  ];
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
  const renderBillDetail = useCallback(() => {
    return (
      <BillDetailModal
        billItemsOfBill={billSelected}
        setSeeBillDetail={setSeeBillDetail}
        isLoadingBillItem={isLoadingBillItem}
      />
    );
  }, [isLoadingBillItem, billSelected, setSeeBillDetail]);
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

  useLayoutEffect(() => {
    if (billSelected) {
      setSeeBillDetail((pre) => !pre);
    }
  }, [billSelected, setSeeBillDetail]);
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
            2. Nhà sản xuất
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currOrder?.supplierCode}
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
            {convertUserToName(currOrder?.user)}
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
            {currOrder?.facility?.name}
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

      <CustomModal
        open={isSeeBillDetail}
        toggle={setSeeBillDetail}
        size="sm"
        title="Chi tiết phiếu nhập"
      >
        {renderBillDetail()}
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_SCM_PURCHASE_ORDER_DETAIL";
export default withScreenSecurity(PurchaseOrderDetailScreen, SCR_ID, true);
