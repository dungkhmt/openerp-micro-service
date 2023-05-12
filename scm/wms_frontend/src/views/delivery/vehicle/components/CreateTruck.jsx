import { Button, Stack } from "@mui/material";
import CustomSelect from "components/select/CustomSelect";
import { useCreateTruck } from "controllers/query/delivery-trip-query";
import { useGetAllUsersExist } from "controllers/query/user-query";

const { FormProvider, useForm, Controller } = require("react-hook-form");
const { default: CustomInput } = require("components/input/CustomInput");

const CreateTruck = ({ setIsAdd }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: yupResolver(shipmentSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const createTruckQuery = useCreateTruck();
  const { isLoading: isLoadingUser, data: users } = useGetAllUsersExist();

  const onSubmit = async (data) => {
    let truckParams = {
      capacity: data?.capacity,
      speed: data?.speed,
      transportCostPerUnit: data?.transportCostPerUnit,
      userManaged: data?.userManaged?.name,
      waitingCost: data?.waitingCost,
    };
    await createTruckQuery.mutateAsync(truckParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"capacity"}
          control={control}
          name={"capacity"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Trọng lượng tối đa"}
              isFullWidth={true}
              error={!!errors["capacity"]}
              message={errors["capacity"]?.message}
            />
          )}
        />
        <Controller
          key={"speed"}
          control={control}
          name={"speed"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Tốc độ"}
              isFullWidth={true}
              error={!!errors["speed"]}
              message={errors["speed"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"waitingCost"}
          control={control}
          name={"waitingCost"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Phí chờ đợi"}
              isFullWidth={true}
              error={!!errors["waitingCost"]}
              message={errors["waitingCost"]?.message}
            />
          )}
        />
        <Controller
          key={"transportCostPerUnit"}
          control={control}
          name={"transportCostPerUnit"}
          render={({ field: { onChange, value } }) => (
            <CustomInput
              required={true}
              value={value}
              type={"number"}
              onChange={onChange}
              label={"Phí vận chuyển"}
              isFullWidth={true}
              error={!!errors["transportCostPerUnit"]}
              message={errors["transportCostPerUnit"]?.message}
            />
          )}
        />
      </Stack>
      <Stack>
        <Controller
          key={"userManaged"}
          control={control}
          name={"userManaged"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={
                users
                  ? users.map((usr) => {
                      return {
                        name: usr.id,
                      };
                    })
                  : []
              }
              fullWidth={true}
              loading={isLoadingUser}
              value={value}
              onChange={onChange}
              label={"Người phụ trách"}
              error={!!errors["userManaged"]}
              message={errors["userManaged"]?.message}
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
export default CreateTruck;
