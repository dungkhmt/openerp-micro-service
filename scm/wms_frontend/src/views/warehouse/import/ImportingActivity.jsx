import DeleteIcon from "@mui/icons-material/Delete";
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
import { acceptedOrderCols } from "../LocalConstant";
function ImportingActivityScreen({ screenAuthorization }) {
  let { path } = useRouteMatch();
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const history = useHistory();
  const { height } = useWindowSize();

  const { isLoading, data } = useGetPurchaseOrderList({
    orderStatus: "accepted",
  });

  const handleButtonClick = (params) => {
    history.push(`${path}/purchase-order-detail`, {
      order: params,
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
          ...acceptedOrderCols,
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
    </Box>
  );
}

const SCR_ID = "SCR_IMPORTING_ACTIVITY";
export default withScreenSecurity(ImportingActivityScreen, SCR_ID, true);
