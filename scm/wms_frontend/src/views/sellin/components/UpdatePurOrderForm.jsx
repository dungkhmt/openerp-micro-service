import { Button, InputBase, Stack, Typography } from "@mui/material";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomSelect from "components/select/CustomSelect";
import { useGetProductListNoPaging } from "controllers/query/category-query";
import { useGetFacilityListNoPaging } from "controllers/query/facility-query";
import {
  useGetSellinPrice,
  useUpdatePurchaseOrder,
} from "controllers/query/purchase-order-query";
import { useMemo, useState } from "react";
import { AppColors } from "../../../shared/AppColors";
import { formatVietnameseCurrency } from "../../../utils/GlobalUtils";
import { staticProductFields } from "../LocalConstant";

const {
  FormProvider,
  useForm,
  Controller,
  useWatch,
} = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdatePurOrderForm = ({ setOpenDrawer, createOrder }) => {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 50,
  });
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      supplierCode: createOrder?.supplierCode,
      vat: createOrder?.vat,
      boughtBy: {
        name: createOrder?.facility?.name,
        code: createOrder?.facility?.code,
      },
    },
    // resolver: yupResolver(purchaseOrderSchema),
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
  const { isLoading: isLoadingFacility, data: facility } =
    useGetFacilityListNoPaging();
  const { isLoading: isLoadingProduct, data: product } =
    useGetProductListNoPaging();
  const { isLoading: isLoadingSellinPrice, data: sellinPrice } =
    useGetSellinPrice();
  const updatePurchaseOrderQuery = useUpdatePurchaseOrder();

  const onSubmit = async (data) => {
    let orderParams = {
      boughtBy: data?.boughtBy?.code,
      createdOrderCode: createOrder?.code,
      orderItems: data?.products?.map((pro) => {
        return {
          priceUnit:
            (pro?.price?.priceBeforeVat * (100 + pro?.price?.vat)) / 100,
          productCode: pro?.code,
          quantity: pro?.quantity,
        };
      }),
      supplierCode: data?.supplierCode,
      vat: data?.vat,
    };
    await updatePurchaseOrderQuery.mutateAsync(orderParams);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  const mergeProductWithPrice = useMemo(() => {
    let result = sellinPrice?.map((price) => {
      let productList = product?.filter((pro) => {
        return pro?.code === price?.productEntity?.code;
      });
      return productList?.length > 0
        ? { ...productList?.[0], price: price }
        : null;
    });
    return result?.every((ele) => ele !== null) ? result : null;
  }, [sellinPrice, product]);
  const calculateTotalMoney = useMemo(() => {
    let totalMoney = products?.reduce(
      (accum, curr) =>
        accum +
        ((curr?.price?.priceBeforeVat * (100 + curr?.price?.vat)) / 100) *
          curr?.quantity,
      0
    );
    return totalMoney ? totalMoney : 0;
  }, [products]);
  return (
    <FormProvider {...methods}>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"supplierCode"}
          control={control}
          name={"supplierCode"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Mã NSX"}
              isFullWidth={true}
              error={!!errors["supplierCode"]}
              message={errors["supplierCode"]?.message}
            />
          )}
        />
        <Controller
          key={"vat"}
          control={control}
          name={"vat"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"VAT"}
              isFullWidth={true}
              error={!!errors["vat"]}
              message={errors["vat"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"boughtBy"}
          control={control}
          name={"boughtBy"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={facility ? facility : []}
              fullWidth={true}
              loading={isLoadingFacility}
              value={value}
              onChange={onChange}
              label={"Mua bởi kho"}
              error={!!errors["boughtBy"]}
              message={errors["boughtBy"]?.message}
            />
          )}
        />
      </Stack>
      <CustomDataGrid
        isSelectable={true}
        params={params}
        setParams={setParams}
        // sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoadingProduct}
        totalItem={mergeProductWithPrice?.length}
        // paginationMode="client"
        handlePaginationModelChange={(props) => {
          setParams({
            ...params,
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
        columns={[
          staticProductFields[0],
          staticProductFields[1],
          staticProductFields[2],
          {
            field: "quantity",
            headerName: "Số lượng mua",
            sortable: false,
            minWidth: 150,
            type: "number",
            editable: true,
            headerAlign: "center",
            align: "center",
            flex: 1,
            renderCell: (params) => {
              const product = products?.find((el) => el?.id === params?.id);
              return product ? product?.quantity : "Nhập số lượng";
            },
            renderEditCell: (params) => {
              const index = products?.findIndex((el) => el?.id === params?.id);
              const value = index !== -1 ? products?.[index]?.quantity : null;
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
          staticProductFields[3],
        ]}
        rows={mergeProductWithPrice ? mergeProductWithPrice : []}
        onSelectionChange={(ids) => {
          let results = mergeProductWithPrice?.filter((pro) =>
            ids.includes(pro?.id)
          );
          setValue("products", results);
        }}
      />
      <Stack
        flexDirection={"row"}
        sx={{
          marginTop: 2,
          justifyContent: "flex-start",
          alignItems: "center",
        }}
      >
        <Typography
          sx={{ fontSize: 18, fontWeight: "bold", color: AppColors.secondary }}
        >
          TỔNG TIỀN:
        </Typography>
        <Typography
          sx={{
            fontSize: 20,
            marginLeft: 2,
            fontWeight: "bold",
            color: AppColors.secondary,
          }}
        >
          {formatVietnameseCurrency(calculateTotalMoney)}
        </Typography>
      </Stack>
      <Stack
        direction="row"
        justifyContent={"flex-end"}
        spacing={2}
        sx={{ marginBottom: 4 }}
      >
        <Button onClick={() => reset()} variant={"outlined"}>
          Reset
        </Button>
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          style={{ color: "white" }}
        >
          Submit
        </Button>
      </Stack>
    </FormProvider>
  );
};
export default UpdatePurOrderForm;
