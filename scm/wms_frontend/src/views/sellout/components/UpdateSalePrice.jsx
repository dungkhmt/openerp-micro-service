import { Button, Stack } from "@mui/material";
import CustomInput from "../../../components/input/CustomInput";
import { useUpdateSelloutPrice } from "../../../controllers/query/sale-order-query";

const {
  FormProvider,
  useForm,
  Controller,
  useWatch,
} = require("react-hook-form");

const UpdateSalePrice = ({ setIsAdd, currPrice }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      massDiscount: currPrice?.massDiscount,
      contractDiscount: currPrice?.contractDiscount,
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

  const updateSelloutPriceQuery = useUpdateSelloutPrice();
  const onSubmit = async (data) => {
    let updateSelloutPrice = {
      massDiscount: data?.massDiscount,
      contractDiscount: data?.contractDiscount,
      productCode: currPrice?.productEntity?.code,
      contractTypeCode: currPrice?.contractType?.code,
    };
    await updateSelloutPriceQuery.mutateAsync(updateSelloutPrice);
    setIsAdd((pre) => !pre);
    reset();
  };

  return (
    <FormProvider {...methods}>
      <Stack direction="column" style={{ marginTop: 15 }}>
        <Controller
          key={"massDiscount"}
          control={control}
          name={"massDiscount"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Chiếu khấu mua sỉ"}
              isFullWidth={true}
              error={!!errors["massDiscount"]}
              message={errors["massDiscount"]?.message}
            />
          )}
        />
        <Controller
          key={"contractDiscount"}
          control={control}
          name={"contractDiscount"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Chiết khấu hợp đồng"}
              isFullWidth={true}
              error={!!errors["contractDiscount"]}
              message={errors["contractDiscount"]?.message}
            />
          )}
        />
      </Stack>
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
export default UpdateSalePrice;
