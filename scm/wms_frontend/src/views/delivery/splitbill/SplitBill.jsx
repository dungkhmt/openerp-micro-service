import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Stack, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetDeliveryBillList } from "controllers/query/bill-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useWindowSize } from "react-use";
import { AppColors } from "../../../shared/AppColors";
import { deliveryBillCols } from "../LocalConstant";
function SplitBillScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const { isLoading, data } = useGetDeliveryBillList(params);
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
      color: AppColors.green,
    },
  ];
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
            THÔNG TIN CƠ BẢN
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row", marginTop: 2 }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            1. Tổng đơn giao hàng:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {data?.totalElements}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row", marginBottom: 2 }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            2. Chi tiết
          </Typography>
        </Stack>
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
          ...deliveryBillCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            flex: 1,
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

const SCR_ID = "SCR_SCM_SPLIT_BILL";
export default withScreenSecurity(SplitBillScreen, SCR_ID, true);
