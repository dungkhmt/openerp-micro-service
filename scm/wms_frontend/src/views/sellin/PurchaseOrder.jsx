import AddIcon from "@mui/icons-material/Add";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Box, Button, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import PrimaryButton from "components/button/PrimaryButton";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetPurchaseOrderList,
  useUpdatePurchaseOrderStatus,
} from "controllers/query/purchase-order-query";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import { purchaseOrderCols } from "./LocalConstant";
import CreatePurOrderForm from "./components/CreatePurOrderForm";

function PurchaseOrderScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const [isApproved, setIsApproved] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const { height } = useWindowSize();
  const { isLoading, data } = useGetPurchaseOrderList();
  const updatePurchaseOrderQuery = useUpdatePurchaseOrderStatus({
    orderCode: itemSelected?.code,
  });
  console.log("Updating order: ", itemSelected);
  const handleUpdateOrder = async () => {
    let updateData = {
      status: "accepted",
    };
    if (itemSelected) await updatePurchaseOrderQuery.mutateAsync(updateData);
    setIsApproved((pre) => !pre);
  };
  let actions = [
    {
      title: "Đặt mua",
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
      title: "Sửa",
      callback: async (item) => {
        setOpenDrawer();
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        setIsRemove();
        setItemSelected(item);
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
    },
    {
      title: "Phê duyệt",
      callback: (item) => {
        setIsApproved((pre) => !pre);
        setItemSelected(item);
      },
      icon: <CheckCircleIcon />,
      color: AppColors.green,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
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
          ...purchaseOrderCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            width: 125,
            minWidth: 150,
            maxWidth: 200,
            type: "actions",
            getActions: (params) => {
              return [
                ...extraActions.map((extraAction, index) => (
                  <Action
                    item={params.row}
                    key={index}
                    extraAction={extraAction}
                    onActionCall={extraAction.callback}
                    disabled={params?.row?.status !== ORDERS_STATUS.created}
                  />
                )),
              ];
            },
          },
        ]}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Tạo mới đơn mua hàng"
      >
        <CreatePurOrderForm setIsAdd={setIsAdd} />
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
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin đơn hàng" />
        {/* <UpdateProductForm /> */}
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={(flag) => {}}
      />
    </Box>
  );
}

const SCR_ID = "SCR_PURCHASE_ORDER";
export default withScreenSecurity(PurchaseOrderScreen, SCR_ID, true);
