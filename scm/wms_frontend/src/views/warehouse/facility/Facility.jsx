import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import MapIcon from "@mui/icons-material/Map";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetFacilityInventory,
  useGetFacilityList,
} from "controllers/query/facility-query";
import { useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { Action } from "../../../components/action/Action";
import DraggableDeleteDialog from "../../../components/dialog/DraggableDialogs";
import { AppColors } from "../../../shared/AppColors";
import { staticProductFields, staticWarehouseCols } from "../LocalConstant";
import CreateFacilityForm from "./components/CreateFacilityForm";

function FacilityScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  const [isOpenEditDrawer, setOpenEditDrawer] = useToggle(false);
  const [isOpenInventoryDrawer, setOpenInventoryDrawer] = useToggle(false);
  const [facilityCode, setFacilityCode] = useState("");
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const history = useHistory();
  let { path } = useRouteMatch();

  const { isLoading, data: facility } = useGetFacilityList(params);
  const { isLoading: isLoadingInventory, data: inventory } =
    useGetFacilityInventory({
      code: facilityCode,
    });

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
      title: "Bản đồ",
      callback: (item) => {
        handleButtonClick();
      },
      icon: <MapIcon />,
      describe: "Xem bản đồ vị trí kho hàng",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        setOpenEditDrawer((pre) => !pre);
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
      title: "Xem tồn kho",
      callback: (item) => {
        setOpenInventoryDrawer();
        setFacilityCode(item?.code);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];

  const handleButtonClick = () => {
    history.push(`${path}/map`, {
      facility: facility?.content,
    });
  };
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
        totalItem={facility?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...staticWarehouseCols,
          {
            field: "quantity",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            minWidth: 200,
            type: "actions",
            flex: 1,
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
        rows={facility ? facility?.content : []}
      />
      <CustomDrawer
        open={isOpenInventoryDrawer}
        onClose={setOpenInventoryDrawer}
      >
        <HeaderModal onClose={setOpenInventoryDrawer} title="Tồn kho" />
        <CustomDataGrid
          isSelectable={false}
          params={params}
          setParams={setParams}
          sx={{
            marginTop: 2,
          }}
          // sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
          isLoading={isLoadingInventory}
          // totalItem={100}
          columns={staticProductFields}
          rows={inventory ? inventory?.content : []}
        />
      </CustomDrawer>
      <CustomModal open={isAdd} toggle={setIsAdd} size="sm" title="Thêm kho">
        <CreateFacilityForm setIsAdd={setIsAdd} />
      </CustomModal>
      <CustomDrawer open={isOpenEditDrawer} onClose={setOpenEditDrawer}>
        <HeaderModal onClose={setOpenEditDrawer} title="Sửa thông tin kho" />
        {/* <UpdateProductForm /> */}
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

const SCR_ID = "SCR_WAREHOUSE";
export default withScreenSecurity(FacilityScreen, SCR_ID, true);
