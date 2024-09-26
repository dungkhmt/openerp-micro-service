import { Button, Stack } from "@mui/material";
import { useUpdateSellinPrice } from "controllers/query/purchase-order-query";
import { unix } from "moment";
import { CustomDatePicker } from "../../../components/datepicker/CustomDatePicker";
import CustomInput from "../../../components/input/CustomInput";

const {
  FormProvider,
  useForm,
  Controller,
  useWatch,
} = require("react-hook-form");

const UpdateProductPrice = ({ setIsAdd, currPrice }) => {
  console.log("Curr price: ", currPrice);
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      endedDate: unix(currPrice?.endedDate),
      priceBeforeVat: currPrice?.priceBeforeVat,
      startedDate: unix(currPrice?.startedDate),
      vat: currPrice?.vat,
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

  const updateSellinPriceQuery = useUpdateSellinPrice();

  const onSubmit = async (data) => {
    let updatedSellinPriceParams = {
      endedDate: unix(currPrice?.endedDate),
      priceBeforeVat: data?.priceBeforeVat,
      startedDate: unix(currPrice?.startedDate),
      vat: data?.vat,
      productCode: currPrice?.productEntity?.code,
    };
    await updateSellinPriceQuery.mutateAsync(updatedSellinPriceParams);
    setIsAdd((pre) => !pre);
    reset();
  };

  return (
    <FormProvider {...methods}>
      <Stack direction="column" style={{ marginTop: 15 }}>
        <Controller
          key={"priceBeforeVat"}
          control={control}
          name={"priceBeforeVat"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Giá trước thuế"}
              isFullWidth={true}
              error={!!errors["priceBeforeVat"]}
              message={errors["priceBeforeVat"]?.message}
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
              label={"Vat"}
              isFullWidth={true}
              error={!!errors["vat"]}
              message={errors["vat"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-between"}>
        <Controller
          key={"startedDate"}
          control={control}
          name={"startedDate"}
          render={({ field: { onChange, value } }) => (
            <CustomDatePicker
              value={value}
              error={!!errors["startedDate"]}
              message={errors["startedDate"]?.message}
              onChange={onChange}
              fullWidth={true}
              label={"Ngày bắt đầu"}

              // minDate={minDate}
            />
          )}
        />
        <Controller
          key={"endedDate"}
          control={control}
          name={"endedDate"}
          render={({ field: { onChange, value } }) => (
            <CustomDatePicker
              value={value}
              error={!!errors["endedDate"]}
              message={errors["endedDate"]?.message}
              onChange={onChange}
              fullWidth={true}
              label={"Ngày kết thúc dự kiến"}
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
export default UpdateProductPrice;
