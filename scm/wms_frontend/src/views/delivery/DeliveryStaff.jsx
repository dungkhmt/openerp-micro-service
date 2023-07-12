import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import { useGetUserPagination } from "../../controllers/query/user-query";
import { deliveryStaff } from "./LocalConstant";

function DeliveryStaffScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
    role: "SCM_DELIVERY_STAFF",
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

  const { isLoading, data: users } = useGetUserPagination(params);
  let actions = [];
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

  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          containFilter={false}
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
        />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={users?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            ...params,
            page: props?.page + 1,
            pageSize: props?.pageSize,
            role: "SCM_DELIVERY_STAFF",
          });
        }}
        columns={[
          ...deliveryStaff,
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
                    disabled={params?.row?.status !== ORDERS_STATUS.created}
                  />
                )),
              ];
            },
          },
        ]}
        rows={users ? users?.content : []}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_DELIVERY_STAFF";
export default withScreenSecurity(DeliveryStaffScreen, SCR_ID, true);
