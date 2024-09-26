import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import InsertLinkIcon from "@mui/icons-material/InsertLink";
import MapIcon from "@mui/icons-material/Map";
import { Box, Button, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import PrimaryButton from "components/button/PrimaryButton";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useDeleteCustomer,
  useGetCustomerList,
  useGetCustomerWithoutPaging,
  useImportCustomer,
} from "controllers/query/category-query";
import { useEffect, useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { useGetAllUsersExist } from "../../../controllers/query/user-query";
import { convertUserToName } from "../../../utils/GlobalUtils";
import { staticCustomerField } from "../LocalConstant";
import CreateCustomerForm from "./components/CreateCustomerForm";
import UpdateCustomerForm from "./components/UpdateCustomerForm";
function CustomerScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });

  const { height } = useWindowSize();
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isAdd, setIsAdd] = useToggle(false);
  const history = useHistory();
  let { path } = useRouteMatch();
  const [isApproved, setIsApproved] = useToggle(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const handleFileChange = (event) => {
    setSelectedFile(event?.target?.files?.[0]);
  };
  const { isLoading, data: customer } = useGetCustomerList(params);
  const deleteCustomerQuery = useDeleteCustomer();
  const importCustomerQuery = useImportCustomer();
  const { isLoading: isLoadingCustomer, data: customerList } =
    useGetCustomerWithoutPaging();
  const { isLoading: isUserLoading, data: users } = useGetAllUsersExist();
  const handleUpload = async () => {
    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);
      importCustomerQuery.mutateAsync(formData);
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
        handleButtonClick();
      },
      icon: <MapIcon />,
      describe: "Xem bản đồ vị trí khách hàng",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        setOpenDrawer((pre) => !pre);
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
  ];

  const filterFields = [
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
      component: "switch",
      name: "status",
      label: "Trạng thái",
      readOnly: false,
      require: true,
    },
    {
      component: "input",
      name: "customerName",
      label: "Tên khách hàng",
      readOnly: false,
      require: true,
    },
    {
      component: "input",
      name: "address",
      label: "Địa chỉ",
      readOnly: false,
      require: true,
    },
  ];
  const handleButtonClick = () => {
    history.push(`${path}/map`, {
      customer: customerList,
    });
  };

  const onSubmit = (data) => {
    setParams({
      ...params,
      createdBy: data?.createdBy?.id,
      customerName: data?.customerName,
      address: data?.address,
      status: data?.status ? "active" : "inactive",
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
          fields={filterFields}
          onSubmit={onSubmit}
        />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={customer?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            ...params,
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...staticCustomerField,
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            minWidth: 200,
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
        rows={customer ? customer?.content : []}
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
        <UpdateCustomerForm
          setOpenDrawer={setOpenDrawer}
          currCustomer={itemSelected}
        />
      </CustomDrawer>
      <DraggableDeleteDialog
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={async (isDelete) => {
          if (isDelete) {
            await deleteCustomerQuery.mutateAsync({ id: itemSelected?.id });
          }
          setIsRemove(false);
        }}
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
        centerTitle="Phê duyệt import khách hàng mới"
        content={
          <Typography color="textSecondary" gutterBottom style={{ padding: 8 }}>
            Bạn có đồng ý phê duyệt tạo khách hàng qua excel file?
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

const SCR_ID = "SCR_SCM_CUSTOMER";
export default withScreenSecurity(CustomerScreen, SCR_ID, true);
