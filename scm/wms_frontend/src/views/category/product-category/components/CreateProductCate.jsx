import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack } from "@mui/material";
import { useCreateProductCategory } from "controllers/query/category-query";
import { productCategorySchema } from "utils/validate";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateProductCate = ({ setIsAdd }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    resolver: yupResolver(productCategorySchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const createProductCategoryQuery = useCreateProductCategory();

  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
    };
    await createProductCategoryQuery.mutateAsync(params);
    setIsAdd((pre) => !pre);
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
          key={"name"}
          control={control}
          name={"name"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tên danh mục"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
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
export default CreateProductCate;
