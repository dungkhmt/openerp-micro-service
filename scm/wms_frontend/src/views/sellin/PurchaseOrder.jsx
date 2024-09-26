import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useDeletePurchaseOrder,
  useGetPurchaseOrderList,
} from "controllers/query/purchase-order-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import { useGetAllUsersExist } from "../../controllers/query/user-query";
import { convertUserToName } from "../../utils/GlobalUtils";
import { purchaseOrderCols } from "./LocalConstant";
import CreatePurOrderForm from "./components/CreatePurOrderForm";
import UpdatePurOrderForm from "./components/UpdatePurOrderForm";

function PurchaseOrderScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const { height } = useWindowSize();
  const history = useHistory();
  let { path } = useRouteMatch();
  const handleButtonClick = (params) => {
    history.push(`${path}/purchase-order-detail`, {
      order: params,
      previous: "purchaseOrderScreen",
    });
  };
  const { isLoading: isUserLoading, data: users } = useGetAllUsersExist();
  const deleteOrderQuery = useDeletePurchaseOrder();
  const STATUS = ["created", "accepted", "delivering", "delivered", "deleted"];
  const { isLoading, data: order } = useGetPurchaseOrderList(params);
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
      title: "Xem",
      callback: (item) => {
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
    },
    {
      title: "Sửa",
      callback: async (item) => {
        setItemSelected(item);
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
  ];
  const filterFields = [
    {
      component: "select",
      name: "createdBy",
      type: "text",
      label: "Người tạo",
      readOnly: false,
      require: true,
      options: users
        ? users?.map((user) => {
            return {
              name: convertUserToName(user),
              id: user?.id,
            };
          })
        : [],
    },
    {
      component: "select",
      name: "status",
      type: "text",
      label: "Trạng thái",
      readOnly: false,
      require: true,
      options: STATUS
        ? STATUS?.map((status) => {
            return {
              name: status,
            };
          })
        : [],
    },
    {
      component: "input",
      name: "facilityName",
      label: "Kho trực thuộc",
      readOnly: false,
      require: true,
    },
    {
      component: "input",
      name: "supplierCode",
      label: "Mã nhà cung cấp",
      readOnly: false,
      require: true,
    },
  ];
  const onSubmit = (data) => {
    setParams({
      ...params,
      createdBy: data?.createdBy?.id,
      facilityName: data?.facilityName,
      supplierCode: data?.supplierCode,
      orderStatus: data?.status?.name.toUpperCase(),
    });
  };
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          onSearch={(keyword) => {
            if (keyword) {
              setParams((pre) => {
                return {
                  ...pre,
                  textSearch: keyword,
                };
              });
            } else {
              setParams((pre) => {
                return {
                  ...pre,
                  textSearch: "",
                };
              });
            }
          }}
          fields={filterFields}
          onSubmit={onSubmit}
        />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={order?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...purchaseOrderCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            minWidth: 150,
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
                    disabled={
                      !(
                        params?.row?.status === ORDERS_STATUS.created ||
                        index === 0
                      ) || params?.row?.status === ORDERS_STATUS.deleted
                    }
                  />
                )),
              ];
            },
          },
        ]}
        rows={order ? order?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Tạo mới đơn mua hàng"
      >
        <CreatePurOrderForm setIsAdd={setIsAdd} />
      </CustomModal>

      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin đơn hàng" />
        <Box sx={{ marginY: 2 }}>
          <UpdatePurOrderForm
            setOpenDrawer={setOpenDrawer}
            createOrder={itemSelected}
          />
        </Box>
      </CustomDrawer>
      <DraggableDeleteDialog
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={async (flag) => {
          if (flag) {
            await deleteOrderQuery.mutateAsync({
              id: itemSelected?.id,
            });
          }
          setIsRemove(false);
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_PURCHASE_ORDER";
export default withScreenSecurity(PurchaseOrderScreen, SCR_ID, true);
