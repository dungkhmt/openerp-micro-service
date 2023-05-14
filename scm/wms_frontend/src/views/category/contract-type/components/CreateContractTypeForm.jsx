import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import {
  useCreateContractType,
  useGetDistChannelList,
} from "controllers/query/category-query";
import { contractType } from "utils/validate";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateContractTypeForm = ({ setIsAdd }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    resolver: yupResolver(contractType),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoadingDistChannel, data: distChannel } = useGetDistChannelList();

  const createContractTypeQuery = useCreateContractType();

  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
      channelCode: data?.channelCode?.code,
    };
    await createContractTypeQuery.mutateAsync(params);
    setIsAdd((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
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
          key={"channelCode"}
          control={control}
          name={"channelCode"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={distChannel ? distChannel?.content : []}
              fullWidth={true}
              loading={isLoadingDistChannel}
              value={value}
              onChange={onChange}
              label={"Kênh phân phối"}
              error={!!errors["channelCode"]}
              message={errors["channelCode"]?.message}
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
export default CreateContractTypeForm;
