import { Box, Button, Stack } from "@mui/material";
import { useUpdateDistChannel } from "../../../../controllers/query/category-query";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateDistChannel = ({ currChannel, setOpenDrawer }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      name: currChannel?.name,
      discount: currChannel?.promotion,
    },
    // resolver: yupResolver(productCategorySchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const updateDistChannel = useUpdateDistChannel({
    id: currChannel?.id,
  });

  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
      promotion: data?.discount,
    };
    await updateDistChannel.mutateAsync(params);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Box sx={{ marginTop: 5 }}></Box>
      <Stack
        direction="row"
        justifyContent={"space-around"}
        spacing={5}
        sx={{ marginY: 2 }}
      >
        <Controller
          key={"name"}
          control={control}
          name={"name"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tên kênh phân phối"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
        <Controller
          key={"discount"}
          control={control}
          name={"discount"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Chiết khấu"}
              isFullWidth={true}
              error={!!errors["discount"]}
              message={errors["discount"]?.message}
            />
          )}
        />
      </Stack>
      <Stack
        direction="row"
        justifyContent={"flex-end"}
        spacing={2}
        sx={{ marginBottom: 2 }}
      >
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
export default UpdateDistChannel;
