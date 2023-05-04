import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Stack, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import { useRef, useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomDrawer from "../../components/drawer/CustomDrawer";
import CustomFormControl from "../../components/form/CustomFormControl";
import CustomMap from "../../components/map/CustomMap";
import CustomModal from "../../components/modal/CustomModal";
import CustomToolBar from "../../components/toolbar/CustomToolBar";
import {
  useCreateFacility,
  useGetFacilityInventory,
  useGetFacilityList,
} from "../../controllers/query/facility-query";
import { useGetAllUsersExist } from "../../controllers/query/user-query";
import useGeoLocation from "../../shared/AppHooks";
import { Action } from "../sellin/PurchaseOrder";
import { staticProductFields, staticWarehouseCols } from "./LocalConstant";

function FacilityScreen({ screenAuthorization }) {
  const currPos = useGeoLocation();
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [facilityCode, setFacilityCode] = useState("");
  const [currMarker, setCurrMarker] = useState(currPos.coordinates);
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: brandSchema,
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = methods;

  const { isLoading, data } = useGetFacilityList();
  const { isLoading: isUserLoading, data: users } = useGetAllUsersExist();
  const { isLoading: isLoadingInventory, data: inventory } =
    useGetFacilityInventory({
      code: facilityCode,
    });
  const createFacilityQuery = useCreateFacility();
  const mapRef = useRef();
  const onSubmit = async (data) => {
    let facilityParams = {
      address: data?.address,
      latitude: data?.map?.lat ? data?.map?.lat : currMarker?.lat,
      longitude: data?.map?.lng ? data?.map?.lng : currMarker?.lng,
      managedBy: data?.managedBy?.name,
      name: data?.name,
    };
    await createFacilityQuery.mutateAsync(facilityParams);
    setIsAdd((pre) => !pre);
    reset();
  };

  const fields = [
    {
      name: "name",
      label: "Tên kho",
      type: "text",
      component: "input",
    },
    {
      name: "address",
      label: "Địa chỉ",
      type: "text",
      component: "input",
    },
    {
      name: "managedBy",
      label: "Quản lý bởi",
      component: "select",
      options: users
        ? users?.map((user) => {
            return {
              name: user?.id,
            };
          })
        : [],
      loading: isUserLoading,
    },
  ];

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
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        // setItemSelected(item);
        // setIsEdit();
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
    {
      title: "Xem",
      callback: (item) => {
        setOpenDrawer((pre) => !pre);
        setFacilityCode(item?.code);
      },
      icon: <VisibilityIcon />,
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
          ...staticWarehouseCols,
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
      <CustomDrawer
        open={isOpenDrawer}
        onClose={setOpenDrawer}
        // style={{ zIndex: 1000 }}
      >
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
            color={green[800]}
            fontSize={17}
          >
            {"TỒN KHO"}
          </Typography>
        </Box>
        <CustomDataGrid
          isSelectable={false}
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
          isLoading={isLoadingInventory}
          totalItem={100}
          columns={staticProductFields}
          rows={inventory ? inventory?.content : []}
        />
      </CustomDrawer>
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
          <Typography>Lấy vị trí</Typography>
          <Stack direction={"row"}>
            <Controller
              key={"map"}
              control={control}
              name={"map"}
              render={({ field: { onChange, value } }) => (
                <CustomMap
                  style={{ width: "30vw", height: "30vh" }}
                  location={currPos}
                  mapRef={mapRef}
                  onChange={(currLoc) => {
                    setCurrMarker(currLoc);
                  }}
                />
              )}
            />
            <Stack direction={"column"}>
              <Button
                onClick={() => {
                  setValue("map", currMarker);
                }}
                variant="contained"
                style={{
                  margin: "20px 20px",
                  color: "white",
                  maxWidth: 100,
                  maxHeight: 40,
                }}
              >
                Lấy vị trí
              </Button>
              <Typography>{`${currMarker.lat}, ${currMarker.lng}`}</Typography>
            </Stack>
          </Stack>
          {/* </Stack> */}
        </FormProvider>
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          style={{ margin: "20px 20px", color: "white" }}
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

const SCR_ID = "SCR_WAREHOUSE";
export default withScreenSecurity(FacilityScreen, SCR_ID, true);
