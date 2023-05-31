import { Box, Divider, Stack, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetFacilityCustomersPaging } from "controllers/query/facility-query";
import { unix } from "moment";
import { useState } from "react";
import { useLocation } from "react-router-dom";
import { useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { staticCustomerField } from "../../category/LocalConstant";
function FacilityDetailScreen() {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const location = useLocation();
  const facility = location.state.facility;
  const { isLoading, data: facilityCustomers } = useGetFacilityCustomersPaging({
    facilityCode: facility?.code,
    ...params,
  });
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={[]} containSearch={false} />
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
            THÔNG TIN KHO HÀNG
          </Typography>
        </Stack>
        <Typography
          sx={{
            fontSize: 16,
            marginTop: 2,
          }}
        >
          1. Mã kho: {facility?.code}
        </Typography>
        <Typography>
          2. Tên kho:
          {facility?.name}
        </Typography>
        <Typography>
          3. Người quản lý:
          {facility?.manager?.firstName || "" + facility?.creator?.lastName}
        </Typography>
        <Typography>
          4. Người tạo:
          {facility?.creator?.firstName || "" + facility?.creator?.lastName}
        </Typography>
        <Typography>
          5. Thời gian tạo: {unix(facility?.createdDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>6. Địa chỉ: {facility?.address}</Typography>
        <Typography sx={{ marginBottom: 2 }}>7. Chi tiết</Typography>
      </Box>
      <Stack
        direction={"row"}
        alignItems={"center"}
        justifyContent={"center"}
      ></Stack>
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
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
            DANH SÁCH KHÁCH HÀNG TRỰC THUỘC
          </Typography>
        </Stack>
        <CustomDataGrid
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
          isLoading={isLoading}
          totalItem={facilityCustomers?.totalElements}
          handlePaginationModelChange={(props) => {
            setParams({
              page: props?.page + 1,
              pageSize: props?.pageSize,
            });
          }}
          columns={[
            ...staticCustomerField,
            // {
            //   field: "action",
            //   headerName: "Hành động",
            //   headerAlign: "center",
            //   align: "center",
            //   sortable: false,
            //   flex: 1,
            //   type: "actions",
            //   getActions: (params) => {
            //     return [
            //       ...extraActions.map((extraAction, index) => (
            //         <Action
            //           item={params.row}
            //           key={index}
            //           extraAction={extraAction}
            //           onActionCall={extraAction.callback}
            //         />
            //       )),
            //     ];
            //   },
            // },
          ]}
          rows={facilityCustomers?.content ? facilityCustomers?.content : []}
        />
      </Box>
    </Box>
  );
}

const SCR_ID = "SCR_FACILITY_DETAIL";
export default withScreenSecurity(FacilityDetailScreen, SCR_ID, true);
