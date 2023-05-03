import AddIcon from "@mui/icons-material/Add";
import { Box, Button, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import {
  useCreateTruck,
  useGetListTruck,
} from "../../controllers/query/delivery-trip-query";
import { useGetAllUsersExist } from "../../controllers/query/user-query";
var columns = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 200,
  },
  {
    field: "capacity",
    headerName: "Trọng lượng chuyên chở",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "transportCostPerUnit",
    headerName: "Phí vận chuyển",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "waitingCost",
    headerName: "Phí chờ đợi",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "speed",
    headerName: "Vận tốc TB",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "",
    headerName: "Hành động",
    sortable: false,
    minWidth: 100,
  },
];

function TruckScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const { isLoading, data, isRefetching, isPreviousData } = useGetListTruck();
  const { isLoading: isLoadingUser, data: users } = useGetAllUsersExist();
  const status = [{ name: "active" }, { name: "inactive" }];
  const createTruckQuery = useCreateTruck();
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
  const onSubmit = async (data) => {
    let truckParams = {
      capacity: data?.capacity,
      speed: data?.speed,
      transportCostPerUnit: data?.transportCostPerUnit,
      userManaged: data?.userManaged?.name,
      waitingCost: data?.waitingCost,
    };
    const res = await createTruckQuery.mutateAsync(truckParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  let actions = [
    {
      title: "Thêm",
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
  const fields = [
    {
      name: "capacity",
      label: "Trọng lượng tối đa",
      type: "number",
      component: "input",
    },
    {
      name: "speed",
      label: "Tốc độ",
      type: "number",
      component: "input",
    },
    {
      name: "transportCostPerUnit",
      label: "Phí vận chuyển",
      type: "number",
      component: "input",
    },
    {
      name: "waitingCost",
      label: "Phí chờ đợi",
      type: "number",
      component: "input",
    },
    {
      name: "userManaged",
      label: "Nhân viên quản lý",
      component: "select",
      options: users
        ? users.map((usr) => {
            return {
              name: usr.id,
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
          {"QUẢN LÝ XE TẢI"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        isSerial
        // initialState={{
        //   pagination: {
        //     paginationModel: { pageSize: 5, page: 0 },
        //   },
        // }}
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoading || isRefetching || isPreviousData}
        totalItem={100}
        columns={columns}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2 }}
      >
        <FormProvider {...methods}>
          {/* <Stack spacing={2}> */}
          <CustomFormControl
            control={control}
            errors={errors}
            fields={fields}
          />
          {/* </Stack> */}
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

const SCR_ID = "SCR_TRUCK";
export default withScreenSecurity(TruckScreen, SCR_ID, true);
