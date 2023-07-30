import { Button, InputBase, Stack } from "@mui/material";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import { useGetProductListNoPaging } from "controllers/query/category-query";
import { useGetPurchaseOrderItems } from "controllers/query/purchase-order-query";
import moment from "moment";
import { useState } from "react";
import { CustomDatePicker } from "../../../components/datepicker/CustomDatePicker";
import { useCreateReceiptBill } from "../../../controllers/query/bill-query";
import { staticProductFields } from "../LocalConstant";

const {
  FormProvider,
  useForm,
  Controller,
  useWatch,
} = require("react-hook-form");

const CreatePurchaseBill = ({ setIsAdd, currOrder }) => {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      products: [],
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
  const { isLoading: isLoadingProduct, data: product } =
    useGetProductListNoPaging();
  const { isLoading, data: orderItem } = useGetPurchaseOrderItems({
    orderCode: currOrder?.code,
  });

  const createBillQuery = useCreateReceiptBill();

  const onSubmit = async (data) => {
    let importParams = {
      orderCode: currOrder?.code,
      importItems: data?.products?.map((pro) => {
        return {
          expireDate: moment(pro?.expired_date).format("YYYY-MM-DD"),
          effectQty: pro?.quantity,
          productCode: pro?.code,
        };
      }),
    };
    await createBillQuery.mutateAsync(importParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <CustomDataGrid
        isSelectable={true}
        params={params}
        setParams={setParams}
        // sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoadingProduct}
        totalItem={100}
        columns={[
          staticProductFields[0],
          // staticProductFields[1],
          // staticProductFields[2],
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
          {
            field: "expireDate",
            headerName: "Ngày hết hạn",
            sortable: false,
            minWidth: 150,
            type: "date",
            editable: true,
            renderCell: (params) => {
              const product = products.find((el) => el.id === params.id);
              return product
                ? moment(product?.expired_date).format("DD-MM-YYYY")
                : "Nhập ngày hết hạn";
            },
            renderEditCell: (params) => {
              const index = products?.findIndex((el) => el.id === params.id);
              const value = index !== -1 ? products[index].expired_date : null;
              return (
                <Controller
                  name={`products.${index}.expired_date`}
                  control={control}
                  render={({ field: { onChange } }) => (
                    <CustomDatePicker value={value} onChange={onChange} />
                  )}
                />
              );
            },
          },
          // staticProductFields[2],
        ]}
        rows={
          product
            ? product?.filter((pro) =>
                orderItem?.content
                  ?.map((item) => item?.product?.code)
                  .includes(pro?.code)
              )
            : []
        }
        onSelectionChange={(ids) => {
          let results = product?.filter((pro) => ids.includes(pro?.id));
          setValue("products", results);
        }}
      />
      <Stack
        direction="row"
        justifyContent={"flex-end"}
        spacing={2}
        sx={{ marginY: 2 }}
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
export default CreatePurchaseBill;
