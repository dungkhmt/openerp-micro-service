import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import moment from "moment";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomFormControl from "../../components/form/CustomFormControl";
import CustomModal from "../../components/modal/CustomModal";
import CustomToolBar from "../../components/toolbar/CustomToolBar";
import {
  useGetFacilityInventory,
  useGetFacilityList,
} from "../../controllers/query/facility-query";
import { useCreateShipment } from "../../controllers/query/shipment-query";
import { Action } from "../sellin/PurchaseOrder";
function ShipmentScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();

  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [facilityCode, setFacilityCode] = useState("");
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

  const { isLoading, data } = useGetFacilityList();
  const { isLoading: isLoadingInventory, data: inventory } =
    useGetFacilityInventory({
      code: facilityCode,
    });
  const createShipmentQuery = useCreateShipment();

  const onSubmit = async (data) => {
    let shipmentParams = {
      endedDate: moment(data?.endDate).format("DD-MM-YYYY"),
      maxSize: data?.maxSize,
      startedDate: moment(data?.startDate).format("DD-MM-YYYY"),
      title: data?.title,
    };
    await createShipmentQuery.mutateAsync(shipmentParams);
    setIsAdd((pre) => !pre);
    reset();
  };

  const fields = [
    {
      name: "title",
      label: "Tiêu đề",
      type: "text",
      component: "input",
    },
    {
      name: "maxSize",
      label: "Số đơn hàng tối đa",
      type: "text",
      component: "input",
    },
    {
      name: "startDate",
      label: "Ngày bắt đầu",
      component: "date",
    },
    {
      name: "endDate",
      label: "Ngày kết thúc",
      component: "date",
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
        console.log("Item: ", item);
        setOpenDrawer((pre) => !pre);
        setFacilityCode(item?.code);
      },
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
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
          color={green[800]}
          fontSize={17}
        >
          {"KẾ HOẠCH GIAO HÀNG"}
        </Typography>
      </Box>
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
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2, zIndex: 3 }}
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

const SCR_ID = "SCR_SHIPMENT";
export default withScreenSecurity(ShipmentScreen, SCR_ID, true);
