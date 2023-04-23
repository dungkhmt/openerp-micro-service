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
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomFormControl from "components/form/CustomFormControl";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useGetProductList } from "controllers/query/category-query";
import { useGetFacilityList } from "controllers/query/facility-query";
import {
  useCreatePurchaseOrder,
  useGetPurchaseOrderList,
} from "controllers/query/purchase-order-query";
import { useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import {
  staticDatagridCols,
  staticFormControlFields,
  staticProductFields,
} from "./LocalConstant";

function PurchaseOrderScreen({ screenAuthorization }) {
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
      title: "Sửa",
      callback: (item) => {},
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
          ...staticDatagridCols,
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
                onActionCall={() => {
                  console.log("Actions");
                }}
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
            fields={[
              ...staticFormControlFields,
              {
                name: "boughtBy",
                label: "Mua bởi kho",
                component: "select",
                options: facility ? facility?.content : [],
                loading: isLoadingFacility,
              },
            ]}
          />
          <CustomDataGrid
            isSelectable={true}
            params={params}
            setParams={setParams}
            sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
            isLoading={isLoadingProduct}
            totalItem={100}
            columns={[
              ...staticProductFields,
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
                  const index = products?.findIndex(
                    (el) => el.id === params.id
                  );
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
            ]}
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
export const Action = ({ extraAction, item, disabled, onActionCall }) => {
  return (
    <IconButton
      size="small"
      disabled={disabled}
      onClick={() => {
        onActionCall(item);
      }}
    >
      <Tooltip title={extraAction.title}>{extraAction.icon}</Tooltip>
    </IconButton>
  );
};
