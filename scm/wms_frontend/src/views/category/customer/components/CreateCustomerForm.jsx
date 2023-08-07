import { Button, Stack, Typography } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import {
  useCreateCustomer,
  useGetContractType,
  useGetCustomerType,
} from "controllers/query/category-query";
import { useRef, useState } from "react";
import CustomMap from "../../../../components/map/CustomMap";
import SearchBoxMap from "../../../../components/map/SearchBoxMap";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateCustomerForm = ({ setIsAdd }) => {
  const status = [{ name: "active" }, { name: "inactive" }];
  const mapRef = useRef();
  const [selectPosition, setSelectPosition] = useState({
    lat: 20.991322,
    lng: 105.839077,
  });
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: yupResolver(customerSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isLoadingCustomerType, data: customerType } =
    useGetCustomerType();
  const { isLoading: isLoadingContractType, data: contractType } =
    useGetContractType();
  const createCustomerQuery = useCreateCustomer();

  const onSubmit = async (data) => {
    let customerParams = {
      address: data?.address,
      contractTypeCode: data?.contractType?.code,
      customerTypeCode: data?.customerType?.code,
      status: data?.status?.name,
      name: data?.name,
      phone: data?.phone,
      latitude: selectPosition?.lat.toString(),
      longitude: selectPosition?.lng.toString(),
      province: data?.province,
    };
    await createCustomerQuery.mutateAsync(customerParams);
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
          key={"address"}
          control={control}
          name={"address"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Địa chỉ"}
              isFullWidth={true}
              error={!!errors["address"]}
              message={errors["address"]?.message}
            />
          )}
        />
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
              label={"Tên khách hàng"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
      </Stack>
      <Stack
        direction="row"
        justifyContent={"space-around"}
        spacing={5}
        sx={{ marginY: 2 }}
      >
        <Controller
          key={"province"}
          control={control}
          name={"province"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Tỉnh thành"}
              isFullWidth={true}
              error={!!errors["province"]}
              message={errors["province"]?.message}
            />
          )}
        />
        <Controller
          key={"phone"}
          control={control}
          name={"phone"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"text"}
              onChange={onChange}
              label={"Số điện thoại"}
              isFullWidth={true}
              error={!!errors["phone"]}
              message={errors["phone"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"customerType"}
          control={control}
          name={"customerType"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={customerType ? customerType?.content : []}
              fullWidth={true}
              loading={isLoadingCustomerType}
              value={value}
              onChange={onChange}
              label={"Loại khách hàng"}
              error={!!errors["customerType"]}
              message={errors["customerType"]?.message}
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
          key={"contractType"}
          control={control}
          name={"contractType"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={contractType ? contractType?.content : []}
              fullWidth={true}
              loading={isLoadingContractType}
              value={value}
              onChange={onChange}
              label={"Loại hợp đồng"}
              error={!!errors["contractType"]}
              message={errors["contractType"]?.message}
            />
          )}
        />
      </Stack>
      <Typography>Lấy vị trí</Typography>
      <SearchBoxMap
        selectPosition={selectPosition}
        setSelectPosition={setSelectPosition}
      />
      <Stack direction={"row"}>
        <Controller
          key={"map"}
          control={control}
          name={"map"}
          render={({ field: { onChange, value } }) => (
            <CustomMap
              style={{ width: "50vw", height: "50vh" }}
              location={selectPosition}
              mapRef={mapRef}
              onChange={onChange}
              setSelectPosition={setSelectPosition}
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
export default CreateCustomerForm;
