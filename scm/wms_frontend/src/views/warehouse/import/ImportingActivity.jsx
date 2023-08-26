import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetPurchaseOrderList } from "controllers/query/purchase-order-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
import { AppColors } from "../../../shared/AppColors";
import { ORDERS_STATUS } from "../../../shared/AppConstants";
import { acceptedOrderCols } from "../LocalConstant";
function ImportingActivityScreen({ screenAuthorization }) {
  let { path } = useRouteMatch();
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const history = useHistory();
  const { height } = useWindowSize();

  const { isLoading, data } = useGetPurchaseOrderList({
    // orderStatus: "accepted",
    ...params,
  });

  const handleButtonClick = (params) => {
    history.push(`${path}/purchase-order-detail`, {
      order: params,
    });
  };
  const onSubmit = (data) => {
    setParams({
      ...params,
      createdBy: data?.createdBy?.name,
      facilityName: data?.facilityName,
      supplierCode: data?.supplierCode,
    });
  };
  let actions = [];
  const extraActions = [
    {
      title: "Xem chi tiết",
      callback: (item) => {
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    // {
    //   title: "Xóa",
    //   callback: (item) => {
    //     // setIsRemove();
    //     // setItemSelected(item);
    //   },
    //   icon: <DeleteIcon />,
    //   color: AppColors.error,
    //   // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    // },
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
          containSearch={false}
          onSubmit={onSubmit}
        />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={data?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...acceptedOrderCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            minWidth: 125,
            flex: 1,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  onActionCall={extraAction.callback}
                  disabled={
                    params?.row?.status === ORDERS_STATUS.created ||
                    params?.row?.status === ORDERS_STATUS.deleted
                  }
                />
              )),
            ],
          },
        ]}
        rows={data ? data?.content : []}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_IMPORTING_ACTIVITY";
export default withScreenSecurity(ImportingActivityScreen, SCR_ID, true);
