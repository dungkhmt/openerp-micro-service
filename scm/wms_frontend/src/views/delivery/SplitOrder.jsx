import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateShipment,
  useGetShipmentItems,
} from "controllers/query/shipment-query";
import moment from "moment";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
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

  const { isLoading, data } = useGetShipmentItems();
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
    history.push(`${path}/add-new`);
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
      callback: (item) => {
        handleButtonClick(item);
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
      callback: (item) => {},
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
    </Box>
  );
}

const SCR_ID = "SCR_SPLIT_ORDER";
export default withScreenSecurity(SplitOrderScreen, SCR_ID, true);
