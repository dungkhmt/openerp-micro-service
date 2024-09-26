import { Button, InputBase, Stack } from "@mui/material";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import { useState } from "react";
import { useCreateSplitBillItem } from "../../../../controllers/query/bill-query";

const {
  FormProvider,
  useForm,
  Controller,
  useWatch,
} = require("react-hook-form");

const SplitBillForm = ({
  setIsAdd,
  currBills,
  deliveryBillItems,
  splittedBillItems,
}) => {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
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
  const createSplitBillQuery = useCreateSplitBillItem();

  const onSubmit = async (data) => {
    let splitParams = {
      billItemDTOS: data?.products?.map((pro) => {
        return {
          quantity: pro?.quantity,
          deliveryBillItemSeqId: pro?.seqId,
          productCode: pro?.product?.code,
        };
      }),
      deliveryBillCode: currBills?.code,
    };
    // TODO: Why this do not render data updated
    await createSplitBillQuery.mutateAsync(splitParams);
    setIsAdd((pre) => !pre);
    reset();
  };

  return (
    <FormProvider {...methods}>
      <CustomDataGrid
        isSelectable={true}
        params={params}
        setParams={setParams}
        hideFooterPagination={true}
        columns={[
          {
            field: "productName",
            headerName: "Tên sản phẩm",
            headerAlign: "center",
            align: "center",
            sortable: false,
            pinnable: true,
            minWidth: 200,
            valueGetter: (params) => {
              return params?.row?.product?.name;
            },
          },
          {
            field: "formerQty",
            headerAlign: "center",
            align: "center",
            headerName: "Lượng ban đầu",
            sortable: false,
            pinnable: true,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.effectiveQty;
            },
          },
          {
            field: "splittedQty",
            headerAlign: "center",
            align: "center",
            headerName: "Lượng đã chia",
            sortable: false,
            pinnable: true,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.splittedQty;
            },
          },
          {
            field: "splitQty",
            headerName: "Lượng định chia",
            sortable: false,
            type: "number",
            editable: true,
            flex: 1,
            renderCell: (params) => {
              const product = products.find((el) => el?.id === params.id);
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
        ]}
        rows={deliveryBillItems?.map((item) => {
          let filteredSplittedItem = splittedBillItems?.filter(
            (i) => i?.deliveryBillItemSeqId === item?.seqId
          );
          let splittedQty = filteredSplittedItem?.reduce(
            (pre, curr) => pre + curr.quantity,
            0
          );

          return {
            ...item,
            splittedQty: splittedQty,
          };
        })}
        onSelectionChange={(ids) => {
          let results = deliveryBillItems?.filter((pro) =>
            ids.includes(pro?.id)
          );
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
export default SplitBillForm;
