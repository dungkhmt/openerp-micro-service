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
import { convertUserToName } from "../../../utils/GlobalUtils";
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
        <CustomToolBar
          actions={[]}
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
            THÔNG TIN KHO HÀNG
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row", marginTop: 2 }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            1. Mã kho:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {facility?.code}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            2. Tên kho:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {facility?.name}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            3. Người quản lý:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {convertUserToName(facility?.manager)}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            4. Người tạo:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {convertUserToName(facility?.creator)}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            5. Thời gian tạo:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {unix(facility?.createdDate).format("DD-MM-YYYY")}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            6. Địa chỉ:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {facility?.address}
          </Typography>
        </Stack>
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
          columns={[...staticCustomerField]}
          rows={facilityCustomers?.content ? facilityCustomers?.content : []}
        />
      </Box>
    </Box>
  );
}

const SCR_ID = "SCR_SCM_FACILITY_DETAIL";
export default withScreenSecurity(FacilityDetailScreen, SCR_ID, true);
