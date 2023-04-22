import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Box,
  Button,
  IconButton,
  InputBase,
  Tooltip,
  Typography,
} from "@mui/material";
import { green } from "@mui/material/colors";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomFormControl from "../../components/form/CustomFormControl";
import CustomModal from "../../components/modal/CustomModal";
import { useGetProductList } from "../../controllers/query/category-query";
import { useGetFacilityList } from "../../controllers/query/facility-query";
import {
  useCreatePurchaseOrder,
  useGetPurchaseOrderList,
} from "../../controllers/query/purchase-order-query";
export const Action = ({ extraAction, item, disabled }) => {
  return (
    <IconButton
      size="small"
      disabled={disabled}
      onClick={() => extraAction.callback(item)}
    >
      <Tooltip title={extraAction.title}>{extraAction.icon}</Tooltip>
    </IconButton>
  );
};
function PurchaseOrderScreen({ screenAuthorization }) {
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
  ];
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const { height } = useWindowSize();
  const { isLoading: isLoadingFacility, data: facility } = useGetFacilityList();
  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const { isLoading, data } = useGetPurchaseOrderList();
  const createPurchaseOrderQuery = useCreatePurchaseOrder();
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      products: [],
    },
    // resolver: brandSchema,
  });
  const onSubmit = async (data) => {
    let orderParams = {
      boughtBy: data?.boughtBy?.code,
      orderCode: data?.code,
      orderItems: data?.products?.map((pro) => {
        return {
          priceUnit: 35000,
          productCode: pro?.code,
          quantity: pro?.quantity,
        };
      }),
      supplierCode: data?.supplierCode,
      vat: data?.vat,
    };
    await createPurchaseOrderQuery.mutateAsync(orderParams);
    setIsAdd((pre) => !pre);
    reset();
    console.log("Data: ", data);
  };
  const {
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = methods;
  const products = useWatch({
    control,
    name: "products",
  });
  const fields = [
    {
      name: "code",
      label: "Mã đơn",
      type: "text",
      component: "input",
    },
    {
      name: "supplierCode",
      label: "Mã NSX",
      type: "text",
      component: "input",
    },
    {
      name: "vat",
      label: "VAT",
      type: "text",
      component: "input",
    },
    {
      name: "boughtBy",
      label: "Mua bởi kho",
      component: "select",
      options: facility ? facility?.content : [],
      loading: isLoadingFacility,
      // field_type: "info",
    },
  ];
  console.log("Products: ", products);
  /**
   * @param {import("@mui/x-data-grid").GridColDef[]}
   */
  var product_fields = [
    {
      field: "code",
      headerName: "Mã code",
      sortable: false,
      pinnable: true,
    },
    {
      field: "name",
      headerName: "Tên sản phẩm",
      sortable: false,
      minWidth: 200,
    },
    {
      field: "status",
      headerName: "Trạng thái",
      sortable: false,
      minWidth: 100,
    },
    {
      field: "quantity",
      headerName: "Số lượng mua",
      sortable: false,
      minWidth: 150,
      type: "number",
      editable: true,
      renderCell: (params) => {
        const product = products.find((el) => el.id === params.id);
        return product ? product.quantity : "Nhập số lượng";
      },
      renderEditCell: (params) => {
        const index = products?.findIndex((el) => el.id === params.id);
        const value = index !== -1 ? products[index].quantity : null;
        return (
          <Controller
            name={`products.${index}.quantity`}
            control={control}
            render={({ field: { onChange } }) => (
              <InputBase
                inputProps={{ min: 0 }}
                sx={{
                  "& .MuiInputBase-input": {
                    textAlign: "right",
                    fontSize: 14,
                    "&::placeholder": {
                      fontSize: 13,
                      opacity: 0.7,
                      fontStyle: "italic",
                    },
                  },
                }}
                placeholder="Nhập số lượng"
                value={value}
                onChange={onChange}
              />
            )}
          />
        );
      },
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
          {"ĐƠN HÀNG"}
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
          },
          {
            field: "supplierCode",
            headerName: "Mã nhà sản xuất",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "totalMoney",
            headerName: "Tổng tiền đặt",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "totalPayment",
            headerName: "Tổng tiền trả",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "vat",
            headerName: "VAT",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "createdBy",
            headerName: "Tạo bởi",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.user.id;
            },
          },
          {
            field: "createdDate",
            headerName: "Thời điểm tạo",
            sortable: false,
            minWidth: 150,
          },
          {
            field: "status",
            headerName: "Trạng thái",
            sortable: false,
            minWidth: 150,
            renderCell: (params) => {
              return (
                <Button variant="outlined" color="info">
                  {params?.row?.status}
                </Button>
              );
            },
          },
          {
            field: "facility",
            headerName: "Kho trực thuộc",
            sortable: false,
            minWidth: 150,
            valueGetter: (params) => {
              return params.row.facility.name;
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
        style={{ padding: 2 }}
      >
        <FormProvider {...methods}>
          <CustomFormControl
            control={control}
            errors={errors}
            fields={fields}
          />
          <CustomDataGrid
            isSelectable={true}
            params={params}
            setParams={setParams}
            sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
            isLoading={isLoadingProduct}
            totalItem={100}
            columns={product_fields}
            rows={product ? product?.content : []}
            onSelectionChange={(ids) => {
              let results = product?.content.filter((pro) =>
                ids.includes(pro?.id)
              );
              setValue("products", results);
            }}
          />
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

const SCR_ID = "SCR_PURCHASE_ORDER";
export default withScreenSecurity(PurchaseOrderScreen, SCR_ID, true);
