import { yupResolver } from "@hookform/resolvers/yup";
import { Box, Button, Stack, Typography } from "@mui/material";
import CustomMap from "components/map/CustomMap";
import CustomSelect from "components/select/CustomSelect";
import { useRef, useState } from "react";
import { facilitySchema } from "utils/validate";
import SearchBoxMap from "../../../../components/map/SearchBoxMap";
import { useCreateFacility } from "../../../../controllers/query/facility-query";
import { useGetAllUsersByRoles } from "../../../../controllers/query/user-query";
import { AppColors } from "../../../../shared/AppColors";
import { convertUserToName } from "../../../../utils/GlobalUtils";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateFacilityForm = ({ setIsAdd }) => {
  const mapRef = useRef();
  const [selectPosition, setSelectPosition] = useState({
    lat: 20.991322,
    lng: 105.839077,
  });
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

  const { isLoading: isUserLoading, data: users } = useGetAllUsersByRoles({
    role: "SCM_WAREHOUSE",
  });
  const createFacilityQuery = useCreateFacility();
  const onSubmit = async (data) => {
    let facilityParams = {
      address: data?.address,
      latitude: selectPosition?.lat.toString(),
      longitude: selectPosition?.lng.toString(),
      managedBy: data?.managedBy?.id,
      name: data?.name,
    };
    await createFacilityQuery.mutateAsync(facilityParams);
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
                        name: convertUserToName(user),
                        id: user?.id,
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
      <Box>
        <Typography style={{ color: AppColors.error, fontSize: 14 }}>
          Lấy vị trí
        </Typography>
        <SearchBoxMap
          selectPosition={selectPosition}
          setSelectPosition={setSelectPosition}
        />
      </Box>
      <Stack direction={"row"} justifyContent={"center"}>
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
