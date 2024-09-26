import { Box, Button, Stack, Typography } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import { useRef, useState } from "react";
import CustomMap from "../../../../components/map/CustomMap";
import SearchBoxMap from "../../../../components/map/SearchBoxMap";
import { useUpdateFacility } from "../../../../controllers/query/facility-query";
import { useGetAllUsersByRoles } from "../../../../controllers/query/user-query";
import { AppColors } from "../../../../shared/AppColors";
import { convertUserToName } from "../../../../utils/GlobalUtils";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const UpdateFacilityForm = ({ setOpenDrawer, currFacility }) => {
  const status = [{ name: "active" }, { name: "inactive" }];
  const mapRef = useRef();
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      address: currFacility?.address,
      name: currFacility?.name,
      status:
        currFacility?.status.toLowerCase() === "active" ? status[0] : status[1],
      managedBy: currFacility?.managedBy?.id,
      map: {
        lat: currFacility?.latitude,
        lng: currFacility?.longitude,
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

  const { isLoading: isLoadingManagedBy, data: managedBy } =
    useGetAllUsersByRoles({
      role: "SCM_WAREHOUSE",
    });
  const updateFacilityQuery = useUpdateFacility({
    id: currFacility?.id,
  });

  const onSubmit = async (data) => {
    let facilityParams = {
      address: data?.address,
      status: data?.status?.name,
      name: data?.name,
      latitude: selectPosition?.lat.toString(),
      longitude: selectPosition?.lng.toString(),
      managedBy: data?.managedBy?.id,
    };
    await updateFacilityQuery.mutateAsync(facilityParams);
    setOpenDrawer((pre) => !pre);
    reset();
  };
  const [selectPosition, setSelectPosition] = useState({
    lat: currFacility?.latitude,
    lng: currFacility?.longitude,
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
              label={"Tên kho"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
      </Stack>
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
        key={"managedBy"}
        control={control}
        name={"managedBy"}
        render={({ field: { onChange, value } }) => (
          <CustomSelect
            readOnly={false}
            options={
              managedBy
                ? managedBy.map((u) => {
                    return {
                      name: convertUserToName(u),
                      id: u?.id,
                    };
                  })
                : []
            }
            fullWidth={true}
            loading={isLoadingManagedBy}
            value={value}
            onChange={onChange}
            label={"Người phụ trách"}
            error={!!errors["managedBy"]}
            message={errors["managedBy"]?.message}
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
        sx={{ marginY: 2 }}
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
export default UpdateFacilityForm;
