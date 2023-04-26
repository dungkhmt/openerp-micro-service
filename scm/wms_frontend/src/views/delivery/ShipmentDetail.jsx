import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Divider, Stack, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateDeliveryTrip,
  useGetDeliveryTripList,
} from "controllers/query/delivery-trip-query";
import { useGetAllUsersExist } from "controllers/query/user-query";
import moment, { unix } from "moment";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { Action } from "../sellin/PurchaseOrder";
function ShipmentDetailScreen() {
  const location = useLocation();
  const currShipment = location.state.shipment;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: brandSchema,
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const createTripQuery = useCreateDeliveryTrip();
  const { isLoading, data } = useGetDeliveryTripList({
    shipment_code: currShipment?.code,
  });
  const { isLoading: isLoadingUser, data: userInCharge } =
    useGetAllUsersExist();

  const onSubmit = async (data) => {
    let tripParams = {
      createdDate: moment(data?.startDate).format("DD-MM-YYYY"),
      shipmentCode: currShipment?.code,
      userInCharge: data?.userInCharge?.name,
    };
    await createTripQuery.mutateAsync(tripParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  let actions = [
    {
      title: "Tạo chuyến giao hàng",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
    {
      title: "Sửa",
      callback: () => {
        console.log("call back");
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Xem",
      callback: async (item) => {
        // setIsApproved((pre) => !pre);
        // setUpdateOrder(item);
      },
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Sửa",
      callback: async (item) => {
        // setIsApproved((pre) => !pre);
        // setUpdateOrder(item);
      },
      icon: <EditIcon />,
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
  const fields = [
    {
      name: "startDate",
      label: "Ngày bắt đầu",
      component: "date",
    },
    {
      name: "userInCharge",
      label: "Giao cho",
      component: "select",
      options: userInCharge
        ? userInCharge?.map((user) => {
            return {
              name: user?.id,
            };
          })
        : [],
      loading: isLoadingUser,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          boxShadow: 3,
          margin: "0px -16px 0 -16px",
          paddingX: 2,
          paddingY: 1,
          position: "sticky",
          backgroundColor: "white",
          zIndex: 1000,
        }}
      >
        <Typography
          id="modal-modal-title"
          variant="h6"
          textTransform="capitalize"
          letterSpacing={1}
          fontSize={18}
          sx={{
            fontFamily: "Open Sans",
            color: AppColors.primary,
            fontWeight: "bold",
          }}
        >
          {"CHI TIẾT ĐỢT GIAO HÀNG"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <Box>
        <Typography>Thông tin cơ bản</Typography>
        <Typography></Typography>
        <Typography>1. Mã đợt : {currShipment?.code}</Typography>
        <Typography>
          2. Ngày bắt đầu:
          {unix(currShipment?.startedDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>
          3. Dự kiến kết thúc:
          {unix(currShipment?.endedDate).format("DD-MM-YYYY")}
        </Typography>
        <Typography>4. Số đơn: {currShipment?.maxSize}</Typography>
      </Box>
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
      <Typography
        id="modal-modal-title"
        variant="h6"
        textTransform="capitalize"
        letterSpacing={1}
        fontSize={18}
        sx={{
          fontFamily: "Open Sans",
          color: AppColors.primary,
          fontWeight: "bold",
        }}
      >
        {"DANH SÁCH CHUYẾN GIAO HÀNG"}
      </Typography>
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
            field: "startedDate",
            headerName: "Ngày bắt đầu",
            sortable: false,
            minWidth: 150,
            valueGetter: (item) => {
              return unix(item?.row?.startedDate).format("DD/MM/YYYY");
            },
          },
          {
            field: "createdBy",
            headerName: "Người tạo",
            sortable: false,
            minWidth: 150,
            valueGetter: (item) => {
              return item?.row?.creator?.id;
            },
          },
          {
            field: "userInCharge",
            headerName: "Người thực hiện",
            sortable: false,
            minWidth: 150,
            valueGetter: (item) => {
              return item?.row?.userInCharge?.id;
            },
          },
          {
            field: "shipment",
            headerName: "Mã đợt giao",
            sortable: false,
            minWidth: 150,
            valueGetter: (item) => {
              return item?.row?.shipment?.code;
            },
          },
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
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2, zIndex: 3 }}
      >
        <FormProvider {...methods}>
          <Stack spacing={5}>
            <CustomFormControl
              control={control}
              errors={errors}
              fields={fields}
            />
          </Stack>
        </FormProvider>

        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          style={{ marginRight: 20, color: "white" }}
        >
          Submit
        </Button>
        <Button onClick={() => reset()} variant={"outlined"}>
          Reset
        </Button>
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_SHIPMENT_DETAIL";
export default withScreenSecurity(ShipmentDetailScreen, SCR_ID, true);
