import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateShipment,
  useGetShipmentList,
} from "controllers/query/shipment-query";
import moment, { unix } from "moment";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
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
  const history = useHistory();
  let { path } = useRouteMatch();
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

  const { isLoading, data } = useGetShipmentList();
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

  const handleButtonClick = (params) => {
    history.push(`${path}/shipment-detail`, {
      shipment: params,
    });
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
        handleButtonClick(item);
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
          {
            field: "code",
            headerName: "Mã đợt",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "title",
            headerName: "Tên đợt giao",
            sortable: false,
            minWidth: 200,
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
            field: "endedDate",
            headerName: "Ngày kết thúc dự kiến",
            sortable: false,
            minWidth: 150,
            valueGetter: (item) => {
              return unix(item?.row?.endedDate).format("DD/MM/YYYY");
            },
          },
          {
            field: "quantity",
            headerName: "Hành động",
            sortable: false,
            minWidth: 150,
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
