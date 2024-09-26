import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
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
  useGetProductCateList,
  useGetProductList,
  useGetProductUnitList,
  useImportCustomer,
} from "controllers/query/category-query";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import PrimaryButton from "../../../components/button/PrimaryButton";
import CustomizedDialogs from "../../../components/dialog/CustomizedDialogs";
import { productColumns } from "../LocalConstant";
import CreateProductForm from "./components/CreateProductForm";
import UpdateProductForm from "./components/UpdateProductForm";
function ProductScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const { height } = useWindowSize();
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isApproved, setIsApproved] = useToggle(false);
  const handleFileChange = (event) => {
    setSelectedFile(event?.target?.files?.[0]);
  };
  // TODO: Change it to import product.
  const importCustomerQuery = useImportCustomer();
  const { isLoading, data, isRefetching, isPreviousData } =
    useGetProductList(params);
  const { isLoading: isLoadingCategory, data: category } =
    useGetProductCateList();
  const { isLoading: isLoadingUnit, data: unit } = useGetProductUnitList();
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
    // {
    //   title: "Import",
    //   callback: (res) => {
    //     handleFileChange(res);
    //   },
    //   icon: <InsertLinkIcon />,
    //   describe: "Import từ file",
    //   disabled: false,
    //   type: "file",
    // },
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
      name: "category",
      type: "text",
      label: "Loại sản phẩm",
      readOnly: false,
      require: true,
      options: category
        ? category?.content?.map((cate) => {
            return {
              name: cate?.name,
              code: cate?.code,
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
      name: "productName",
      label: "Tên sản phẩm",
      readOnly: false,
      require: true,
    },
    {
      component: "select",
      name: "unitCode",
      type: "text",
      label: "Đơn vị",
      readOnly: false,
      require: true,
      options: unit
        ? unit?.content?.map((unit) => {
            return {
              name: unit?.name,
              code: unit?.code,
            };
          })
        : [],
    },
  ];
  const onSubmit = (data) => {
    setParams({
      ...params,
      categoryCode: data?.category?.code,
      unitCode: data?.unitCode?.code,
      productName: data?.productName,
      status: data?.status ? "active" : "inactive",
    });
  };
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
        // isSerial
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoading || isRefetching || isPreviousData}
        totalItem={data?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            ...params,
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          ...productColumns,
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
        title="Thêm sản phẩm"
      >
        <CreateProductForm setIsAdd={setIsAdd} />
      </CustomModal>
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin sản phẩm" />
        <UpdateProductForm
          setOpenDrawer={setOpenDrawer}
          currProduct={itemSelected}
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
        centerTitle="Phê duyệt import cusomter"
        content={
          <Typography color="textSecondary" gutterBottom style={{ padding: 8 }}>
            Bạn có đồng ý phê duyệt tạo sản phẩm qua excel file?
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

const SCR_ID = "SCR_SCM_PRODUCT";
export default withScreenSecurity(ProductScreen, SCR_ID, true);
