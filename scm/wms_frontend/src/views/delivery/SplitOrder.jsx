import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useCreateShipment } from "controllers/query/shipment-query";
import moment from "moment";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { useGetDeliveryBillList } from "../../controllers/query/bill-query";
import { Action } from "../sellin/PurchaseOrder";
function SplitOrderScreen({ screenAuthorization }) {
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

  const { isLoading, data } = useGetDeliveryBillList();
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
    history.push(`${path}/split-bill-detail`, {
      bills: params,
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
      title: "Xem",
      callback: (item) => {
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
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
          {"DANH SÁCH ĐƠN GIAO HÀNG"}
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

const SCR_ID = "SCR_SPLIT_ORDER";
export default withScreenSecurity(SplitOrderScreen, SCR_ID, true);
