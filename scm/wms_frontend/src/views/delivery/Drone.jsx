import AddIcon from "@mui/icons-material/Add";
import { Box, Button } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateDrone,
  useGetDroneList,
} from "controllers/query/delivery-trip-query";
import { useGetAllUsersExist } from "controllers/query/user-query";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
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

function DroneScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const { isLoading, data, isRefetching, isPreviousData } = useGetDroneList();
  const { isLoading: isLoadingUser, data: users } = useGetAllUsersExist();
  const createDroneQuery = useCreateDrone();
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
    let droneParams = {
      duration: data?.duration,
      capacity: data?.capacity,
      speed: data?.speed,
      transportCostPerUnit: data?.transportCostPerUnit,
      userManaged: data?.userManaged?.name,
      waitingCost: data?.waitingCost,
    };
    await createDroneQuery.mutateAsync(droneParams);
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
      name: "duration",
      label: "Thời gian hoạt động",
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

const SCR_ID = "SCR_DRONE";
export default withScreenSecurity(DroneScreen, SCR_ID, true);
