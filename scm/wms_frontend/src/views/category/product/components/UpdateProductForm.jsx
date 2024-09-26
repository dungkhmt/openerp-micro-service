import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import {
  useGetProductCateList,
  useGetProductUnitList,
  useUpdateProduct,
} from "controllers/query/category-query";
import { productSchema } from "utils/validate";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateProductForm = ({ setOpenDrawer, currProduct }) => {
  const status = [{ name: "active" }, { name: "inactive" }];

  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      name: currProduct?.name,
      sku: currProduct?.sku,
      brand: currProduct?.brand,
      unitPerBox: currProduct?.unitPerBox,
      status: currProduct?.status === "active" ? status[0] : status[1],
      unitId: currProduct?.productUnit,
      categoryId: currProduct?.productCategory,
      massQuantity: currProduct?.massQuantity,
    },
    resolver: yupResolver(productSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isLoadingProductCate, data: productCate } =
    useGetProductCateList();
  const { isLoading: isLoadingProductUnit, data: productUnit } =
    useGetProductUnitList();
  const updateProductQuery = useUpdateProduct({
    id: currProduct?.id,
  });

  const onSubmit = async (data) => {
    let productParams = {
      brand: data?.brand,
      categoryId: data?.categoryId?.id,
      name: data?.name,
      sku: data?.sku,
      status: data?.status?.name,
      unitId: data?.unitId?.id,
      unitPerBox: data?.unitPerBox,
      massQuantity: data?.massQuantity,
    };
    setOpenDrawer((pre) => !pre);
    await updateProductQuery.mutateAsync(productParams);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Stack
        direction="column"
        justifyContent={"space-around"}
        spacing={2}
        sx={{ marginY: 5 }}
      >
        <Controller
          key={"product"}
          control={control}
          name={"name"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tên sản phẩm"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
        <Controller
          key={"sku"}
          control={control}
          name={"sku"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Mã sku"}
              isFullWidth={true}
              error={!!errors["sku"]}
              message={errors["sku"]?.message}
            />
          )}
        />
        <Controller
          key={"brand"}
          control={control}
          name={"brand"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Nhãn hiệu"}
              isFullWidth={true}
              error={!!errors["brand"]}
              message={errors["brand"]?.message}
            />
          )}
        />
        <Controller
          key={"unitPerBox"}
          control={control}
          name={"unitPerBox"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Số lượng/đơn vị"}
              isFullWidth={true}
              error={!!errors["unitPerBox"]}
              message={errors["unitPerBox"]?.message}
            />
          )}
        />
        <Controller
          key={"categoryId"}
          control={control}
          name={"categoryId"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={productCate ? productCate?.content : []}
              fullWidth={true}
              loading={isLoadingProductCate}
              value={value}
              onChange={onChange}
              label={"Ngành hàng"}
              error={!!errors["categoryId"]}
              message={errors["categoryId"]?.message}
            />
          )}
        />
        <Controller
          key={"status"}
          control={control}
          name={"status"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={status}
              fullWidth={true}
              value={value}
              onChange={onChange}
              label={"Trạng thái"}
              error={!!errors["status"]}
              message={errors["status"]?.message}
            />
          )}
        />
        <Controller
          key={"unitId"}
          control={control}
          name={"unitId"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={productUnit ? productUnit?.content : []}
              fullWidth={true}
              loading={isLoadingProductUnit}
              value={value}
              onChange={onChange}
              label={"Đơn vị tính"}
              error={!!errors["unitId"]}
              message={errors["unitId"]?.message}
            />
          )}
        />
        <Controller
          key={"massQuantity"}
          control={control}
          name={"massQuantity"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Số lượng mua sỉ"}
              isFullWidth={true}
              error={!!errors["massQuantity"]}
              message={errors["massQuantity"]?.message}
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
export default UpdateProductForm;
