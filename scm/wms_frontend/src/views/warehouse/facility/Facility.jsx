import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import InsertLinkIcon from "@mui/icons-material/InsertLink";
import MapIcon from "@mui/icons-material/Map";
import VisibilityIcon from "@mui/icons-material/Visibility";
import WarehouseIcon from "@mui/icons-material/Warehouse";
import { Box, Button, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetFacilityInventory,
  useGetFacilityList,
  useGetFacilityListNoPaging,
  useImportFacility,
} from "controllers/query/facility-query";
import { useEffect, useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import PrimaryButton from "../../../components/button/PrimaryButton";
import CustomizedDialogs from "../../../components/dialog/CustomizedDialogs";
import { useGetAllUsersByRoles } from "../../../controllers/query/user-query";
import { convertUserToName } from "../../../utils/GlobalUtils";
import { staticProductFields, staticWarehouseCols } from "../LocalConstant";
import CreateFacilityForm from "./components/CreateFacilityForm";
import UpdateFacilityForm from "./components/UpdateFacilityForm";
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
  const [selectedFile, setSelectedFile] = useState(null);
  const handleFileChange = (event) => {
    setSelectedFile(event?.target?.files?.[0]);
  };
  const [isApproved, setIsApproved] = useToggle(false);
  let { path } = useRouteMatch();

  const { isLoading, data: facility } = useGetFacilityList(params);
  const { isLoading: isLoadingFacilityList, data: facilityList } =
    useGetFacilityListNoPaging();
  const { isLoading: isLoadingInventory, data: inventory } =
    useGetFacilityInventory({
      code: facilityCode,
    });
  const importFacilityQuery = useImportFacility();
  const { isLoading: isUserLoading, data: users } =
    useGetAllUsersByRoles("SCM_WAREHOUSE");
  const handleUpload = async () => {
    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);
      importFacilityQuery.mutateAsync(formData);
      setSelectedFile(null);
    }
    setIsApproved(false);
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
      title: "Import",
      callback: (res) => {
        handleFileChange(res);
      },
      icon: <InsertLinkIcon />,
      describe: "Import từ file",
      disabled: false,
      type: "file",
    },
    {
      title: "Bản đồ",
      callback: (item) => {
        openMap();
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
        setItemSelected(item);
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
      icon: <WarehouseIcon />,
      color: AppColors.info,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
    {
      title: "Xem chi tiết",
      callback: (item) => {
        seeDetail(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
  ];
  const fields = [
    {
      component: "select",
      name: "createdBy",
      type: "text",
      label: "Người tạo",
      readOnly: false,
      require: true,
      options: users
        ? users?.map((user) => {
            return {
              name: convertUserToName(user),
              id: user?.id,
            };
          })
        : [],
    },
    {
      component: "select",
      name: "managedBy",
      type: "text",
      label: "Thủ kho",
      readOnly: false,
      require: true,
      options: users
        ? users?.map((user) => {
            return {
              name: convertUserToName(user),
              id: user?.id,
            };
          })
        : [],
    },
    {
      component: "switch",
      name: "status",
      label: "Trạng thái",
      readOnly: false,
      require: true,
    },
    {
      component: "input",
      name: "facilityName",
      label: "Tên kho",
      readOnly: false,
      require: true,
    },
  ];
  const onSubmit = (data) => {
    setParams({
      ...params,
      createdBy: data?.createdBy?.id,
      managedBy: data?.managedBy?.id,
      facilityName: data?.facilityName,
      status: data?.status ? "ACTIVE" : "INACTIVE",
    });
  };
  const openMap = () => {
    history.push(`${path}/map`, {
      facility: facilityList,
    });
  };
  const seeDetail = (item) => {
    history.push(`${path}/detail`, {
      facility: item,
    });
  };
  useEffect(() => {
    if (selectedFile) {
      setIsApproved(true);
    }
  }, [selectedFile, setIsApproved]);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          onSearch={(keyword) => {
            if (keyword) {
              setParams((pre) => {
                return {
                  ...pre,
                  textSearch: keyword,
                };
              });
            } else {
              setParams((pre) => {
                return {
                  ...pre,
                  textSearch: "",
                };
              });
            }
          }}
          fields={fields}
          onSubmit={onSubmit}
        />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={facility?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            ...params,
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
        <UpdateFacilityForm
          setOpenDrawer={setOpenEditDrawer}
          currFacility={itemSelected}
        />
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={(flag) => {}}
      />
      <CustomizedDialogs
        open={isApproved}
        handleClose={() => {
          setIsApproved(false, () => {
            setSelectedFile(null);
          });
        }}
        contentTopDivider
        title={selectedFile?.name}
        contentBottomDivider
        centerTitle="Phê duyệt import kho"
        content={
          <Typography color="textSecondary" gutterBottom style={{ padding: 8 }}>
            Bạn có đồng ý phê duyệt tạo kho hàng qua excel file?
          </Typography>
        }
        actions={[
          <Button onClick={setIsApproved}>Hủy bỏ</Button>,
          <PrimaryButton onClick={handleUpload}>Phê duyệt</PrimaryButton>,
        ]}
        customStyles={{
          contents: (theme) => ({ width: "100%" }),
          actions: (theme) => ({ paddingRight: theme.spacing(2) }),
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_FACILITY";
export default withScreenSecurity(FacilityScreen, SCR_ID, true);
