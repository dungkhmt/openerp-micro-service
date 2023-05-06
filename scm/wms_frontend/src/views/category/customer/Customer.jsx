import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetCustomerList } from "controllers/query/category-query";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import DraggableDeleteDialog from "../../../components/dialog/DraggableDialogs";
import CustomDrawer from "../../../components/drawer/CustomDrawer";
import HeaderModal from "../../../components/modal/HeaderModal";
import { staticCustomerField } from "../LocalConstant";
import CreateCustomerForm from "./components/CreateCustomerForm";
function CustomerScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const { isLoading, data } = useGetCustomerList();
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isAdd, setIsAdd] = useToggle(false);
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
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={[
          ...staticCustomerField,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            width: 125,
            minWidth: 150,
            maxWidth: 200,
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
        title="Thêm khách hàng"
      >
        <CreateCustomerForm setIsAdd={setIsAdd} />
      </CustomModal>
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin khách hàng" />
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={(flag) => {}}
      />
    </Box>
  );
}

const SCR_ID = "SCR_CUSTOMER";
export default withScreenSecurity(CustomerScreen, SCR_ID, true);
