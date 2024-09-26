import { Box, Button, Stack } from "@mui/material";
import CustomSelect from "../../../../components/select/CustomSelect";
import {
  useGetDistChannelList,
  useUpdateContractType,
} from "../../../../controllers/query/category-query";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateContractType = ({ currContract, setOpenDrawer }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      name: currContract?.name,
      distributionChannel: currContract?.channel,
    },
    // resolver: yupResolver(productCategorySchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isLoadingDistChannel, data: distChannel } =
    useGetDistChannelList();
  const updateContractType = useUpdateContractType({
    id: currContract?.id,
  });

  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
      channelCode: data?.distributionChannel?.code,
    };
    await updateContractType.mutateAsync(params);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Box sx={{ marginTop: 5 }}></Box>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
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
              label={"Tên hợp đồng"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
        <Controller
          key={"distributionChannel"}
          control={control}
          name={"distributionChannel"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={distChannel ? distChannel?.content : []}
              fullWidth={true}
              loading={isLoadingDistChannel}
              value={value}
              onChange={onChange}
              label={"Kênh phân phối"}
              error={!!errors["distributionChannel"]}
              message={errors["distributionChannel"]?.message}
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
export default UpdateContractType;
