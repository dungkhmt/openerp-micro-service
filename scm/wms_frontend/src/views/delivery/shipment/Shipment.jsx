import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetShipmentList } from "controllers/query/shipment-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { Action } from "../../../components/action/Action";
import { AppColors } from "../../../shared/AppColors";
import { shipmentCols } from "../LocalConstant";
import CreateShipmentForm from "./components/CreateShipmentForm";
function ShipmentScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();

  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isAdd, setIsAdd] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const history = useHistory();
  let { path } = useRouteMatch();

  const { isLoading, data } = useGetShipmentList();

  const handleButtonClick = (params) => {
    history.push(`${path}/shipment-detail`, {
      shipment: params,
    });
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
  ];
  const extraActions = [
    {
      title: "Sửa",
      callback: async (item) => {
        setOpenDrawer();
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        setIsRemove();
        setItemSelected(item);
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
    },
    {
      title: "Xem",
      callback: (item) => {
        handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
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
          ...shipmentCols,

          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            flex: 1,
            type: "actions",
            getActions: (params) => {
              return [
                ...extraActions.map((extraAction, index) => (
                  <Action
                    item={params.row}
                    key={index}
                    extraAction={extraAction}
                    onActionCall={extraAction.callback}
                  />
                )),
              ];
            },
          },
        ]}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Tạo mới đợt giao hàng"
      >
        <CreateShipmentForm setIsAdd={setIsAdd} />
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_SHIPMENT";
export default withScreenSecurity(ShipmentScreen, SCR_ID, true);
