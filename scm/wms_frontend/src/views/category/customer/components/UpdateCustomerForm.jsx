import { Box, Button, Stack, Typography } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import {
  useGetContractType,
  useGetCustomerType,
  useUpdateCustomer,
} from "controllers/query/category-query";
import { useRef, useState } from "react";
import CustomMap from "../../../../components/map/CustomMap";
import SearchBoxMap from "../../../../components/map/SearchBoxMap";
import { AppColors } from "../../../../shared/AppColors";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateCustomerForm = ({ setOpenDrawer, currCustomer }) => {
  const status = [{ name: "active" }, { name: "inactive" }];
  const mapRef = useRef();
  const [currMarker, setCurrMarker] = useState();
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      address: currCustomer?.address,
      name: currCustomer?.name,
      phone: currCustomer?.phone,
      customerType: currCustomer?.customerType,
      status: currCustomer?.status === "active" ? status[0] : status[1],
      contractType: currCustomer?.contractType,
      map: {
        lat: currCustomer?.latitude,
        lng: currCustomer?.longitude,
      },
    },
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
  const updateCustomerQuery = useUpdateCustomer({
    id: currCustomer?.id,
  });

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
    await updateCustomerQuery.mutateAsync(customerParams);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  const [selectPosition, setSelectPosition] = useState({
    lat: currCustomer?.latitude,
    lng: currCustomer?.longitude,
  });
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
      <Box>
        <Typography style={{ color: AppColors.error, fontSize: 14 }}>
          Lấy vị trí
        </Typography>
        <SearchBoxMap
          selectPosition={selectPosition}
          setSelectPosition={setSelectPosition}
        />
      </Box>
      <Stack direction={"row"}>
        <Controller
          key={"map"}
          control={control}
          name={"map"}
          render={({ field: { onChange, value } }) => (
            <CustomMap
              style={{ width: "100%", height: "50vh" }}
              location={selectPosition}
              mapRef={mapRef}
              setSelectPosition={setSelectPosition}
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
export default UpdateCustomerForm;
