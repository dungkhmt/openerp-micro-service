import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack, Typography } from "@mui/material";
import CustomMap from "components/map/CustomMap";
import CustomSelect from "components/select/CustomSelect";
import {
  useCreateCustomer,
  useGetContractType,
  useGetCustomerType,
} from "controllers/query/category-query";
import { useRef, useState } from "react";
import { customerSchema } from "utils/validate";
import { useGeoLocation } from "../../../../shared/AppHooks";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateCustomerForm = ({ setIsAdd }) => {
  const status = [{ name: "active" }, { name: "inactive" }];
  const currPos = useGeoLocation();
  const mapRef = useRef();
  const [currMarker, setCurrMarker] = useState(currPos.coordinates);
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    resolver: yupResolver(customerSchema),
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
      latitude: data?.map?.lat ? data?.map?.lat : currMarker?.lat,
      longitude: data?.map?.lng ? data?.map?.lng : currMarker?.lng,
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
            label={"Ngành hàng"}
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
            label={"Đơn vị tính"}
            error={!!errors["contractType"]}
            message={errors["contractType"]?.message}
          />
        )}
      />
      <Typography>Lấy vị trí</Typography>
      <Stack direction={"row"}>
        <Controller
          key={"map"}
          control={control}
          name={"map"}
          render={({ field: { onChange, value } }) => (
            <CustomMap
              style={{ width: "30vw", height: "30vh" }}
              location={currPos}
              mapRef={mapRef}
              onChange={(currLoc) => {
                setCurrMarker(currLoc);
              }}
            />
          )}
        />
        <Stack direction={"column"}>
          <Typography>{`${currMarker.lat}, ${currMarker.lng}`}</Typography>
        </Stack>
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
export default UpdateCustomerForm;
