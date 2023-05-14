import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack, Typography } from "@mui/material";
import CustomMap from "components/map/CustomMap";
import CustomSelect from "components/select/CustomSelect";
import { useRef, useState } from "react";
import { facilitySchema } from "utils/validate";
import { useCreateFacility } from "../../../../controllers/query/facility-query";
import { useGetAllUsersExist } from "../../../../controllers/query/user-query";
import { useGeoLocation } from "../../../../shared/AppHooks";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateFacilityForm = ({ setIsAdd }) => {
  const currPos = useGeoLocation();
  const mapRef = useRef();
  const [currMarker, setCurrMarker] = useState(currPos.coordinates);
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    resolver: yupResolver(facilitySchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isUserLoading, data: users } = useGetAllUsersExist();
  const createFacilityQuery = useCreateFacility();
  const onSubmit = async (data) => {
    let facilityParams = {
      address: data?.address,
      latitude: data?.map?.lat ? data?.map?.lat : currMarker?.lat,
      longitude: data?.map?.lng ? data?.map?.lng : currMarker?.lng,
      managedBy: data?.managedBy?.name,
      name: data?.name,
    };
    await createFacilityQuery.mutateAsync(facilityParams);
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
              label={"Tên kho"}
              isFullWidth={true}
              error={!!errors["name"]}
              message={errors["name"]?.message}
            />
          )}
        />
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
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"managedBy"}
          control={control}
          name={"managedBy"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={
                users
                  ? users?.map((user) => {
                      return {
                        name: user?.id,
                      };
                    })
                  : []
              }
              fullWidth={true}
              loading={isUserLoading}
              value={value}
              onChange={onChange}
              label={"Quản lý bởi"}
              error={!!errors["managedBy"]}
              message={errors["managedBy"]?.message}
            />
          )}
        />
      </Stack>
      <Typography>Lấy vị trí</Typography>
      <Stack direction={"row"}>
        <Controller
          key={"map"}
          control={control}
          name={"map"}
          render={({ field: { onChange, value } }) => (
            <CustomMap
              style={{ width: "50vw", height: "50vh" }}
              location={currPos}
              mapRef={mapRef}
              onChange={(currLoc) => {
                setCurrMarker(currLoc);
              }}
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
export default CreateFacilityForm;
