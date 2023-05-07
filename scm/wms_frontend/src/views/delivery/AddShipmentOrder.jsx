import AddIcon from "@mui/icons-material/Add";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetDeliveryBillList } from "controllers/query/bill-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
function AddShipmentOrderScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const { isLoading, data } = useGetDeliveryBillList({});
  const history = useHistory();
  let { path } = useRouteMatch();

  const handleButtonClick = (params) => {
    history.push(`${path}/split-bill-detail`, {
      bills: params,
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
      title: "Thêm",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
      },
      icon: <AddIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <Box>
        <Typography>Thông tin cơ bản</Typography>
        <Typography>Tổng số item: {data?.content?.length}</Typography>
      </Box>
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
            minWidth: 150,
          },
          {
            field: "createdDate",
            headerName: "Thời điểm tạo",
            sortable: false,
            minWidth: 200,
          },
          {
            field: "boughtBy",
            headerName: "Mua bởi",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.saleOrder?.customer?.name;
            },
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
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  onActionCall={extraAction.callback}
                  disabled={false}
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

const SCR_ID = "SCR_ADD_SHIPMENT_ORDER";
export default withScreenSecurity(AddShipmentOrderScreen, SCR_ID, true);
