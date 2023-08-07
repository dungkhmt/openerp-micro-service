import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack } from "@mui/material";
import moment from "moment";
import { shipmentSchema } from "utils/validate";
import { CustomDatePicker } from "../../../../components/datepicker/CustomDatePicker";
import { useUpdateShipment } from "../../../../controllers/query/shipment-query";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateShipment = ({ setOpenDrawer, currShipment }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      endedDate: new Date(currShipment?.endedDate).toLocaleDateString("en-US"),
      maxSize: currShipment?.maxSize,
      startedDate: new Date(currShipment?.startedDate).toLocaleDateString(
        "en-US"
      ),
      title: currShipment?.title,
    },
    resolver: yupResolver(shipmentSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const updateShipmentQuery = useUpdateShipment({
    id: currShipment?.id,
  });

  const onSubmit = async (data) => {
    let shipmentParams = {
      maxSize: data?.maxSize,
      startedDate: moment(
        data?.startedDate.substring(3),
        "MMM DD YYYY HH:mm:ss Z"
      ).format("DD-MM-YYYY"),
      endedDate: moment(
        data?.endedDate.substring(3),
        "MMM DD YYYY HH:mm:ss Z"
      ).format("DD-MM-YYYY"),
      title: data?.title,
    };
    await updateShipmentQuery.mutateAsync(shipmentParams);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Stack
        direction="row"
        justifyContent={"space-around"}
        spacing={5}
        sx={{ marginY: 2 }}
      >
        <Controller
          key={"title"}
          control={control}
          name={"title"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tiêu đề"}
              isFullWidth={true}
              error={!!errors["title"]}
              message={errors["title"]?.message}
            />
          )}
        />
        <Controller
          key={"maxSize"}
          control={control}
          name={"maxSize"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Số đơn hàng tối đa"}
              isFullWidth={true}
              error={!!errors["maxSize"]}
              message={errors["maxSize"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
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
              // minDate={minDate}
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
export default UpdateShipment;
