import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useDeleteTruck,
  useGetListTruck,
} from "controllers/query/delivery-trip-query";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { truckCols } from "../LocalConstant";
import CreateTruck from "./components/CreateTruck";
import UpdateTruck from "./components/UpdateTruck";

function TruckScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();

  const [isAdd, setIsAdd] = useToggle(false);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);

  const deleteTruckQuery = useDeleteTruck({
    code: itemSelected?.code,
  });
  const { isLoading, data, isRefetching, isPreviousData } =
    useGetListTruck(params);
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
      callback: (item) => {
        setItemSelected(item);
        setOpenDrawer((pre) => !pre);
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
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          containFilter={false}
          containSearch={false}
        />
      </Box>
      <CustomDataGrid
        // isSerial
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoading || isRefetching || isPreviousData}
        totalItem={data?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...truckCols,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            minWidth: 150,
            flex: 1,
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
        title="Tạo mới xe tải"
      >
        <CreateTruck setIsAdd={setIsAdd} />
      </CustomModal>
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin xe tải" />
        <Box sx={{ marginTop: 2 }}>
          <UpdateTruck setIsAdd={setOpenDrawer} currTruck={itemSelected} />
        </Box>
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={async (flag) => {
          if (flag) {
            await deleteTruckQuery.mutateAsync();
          }
          setIsRemove(false);
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_TRUCK";
export default withScreenSecurity(TruckScreen, SCR_ID, true);
