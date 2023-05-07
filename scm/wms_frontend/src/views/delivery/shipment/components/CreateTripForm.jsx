import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Stack } from "@mui/material";
import { CustomDatePicker } from "components/datepicker/CustomDatePicker";
import CustomSelect from "components/select/CustomSelect";
import { useCreateDeliveryTrip } from "controllers/query/delivery-trip-query";
import { useGetFacilityList } from "controllers/query/facility-query";
import { useGetAllUsersExist } from "controllers/query/user-query";
import moment from "moment";
import { tripSchema } from "utils/validate";

const { FormProvider, useForm, Controller } = require("react-hook-form");

const CreateTripForm = ({ setIsAdd, currShipment }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    resolver: yupResolver(tripSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isLoadingUser, data: userInCharge } =
    useGetAllUsersExist();
  const { isLoading: isLoadingFacility, data: facility } = useGetFacilityList();

  const createTripQuery = useCreateDeliveryTrip();

  const onSubmit = async (data) => {
    let tripParams = {
      createdDate: moment(data?.startDate).format("DD-MM-YYYY"),
      shipmentCode: currShipment?.code,
      userInCharge: data?.userInCharge?.name,
      facilityCode: data?.facility?.code,
    };
    await createTripQuery.mutateAsync(tripParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  return (
    <FormProvider {...methods}>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"userInCharge"}
          control={control}
          name={"userInCharge"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={
                userInCharge
                  ? userInCharge?.map((user) => {
                      return {
                        name: user?.id,
                      };
                    })
                  : []
              }
              fullWidth={true}
              loading={isLoadingUser}
              value={value}
              onChange={onChange}
              label={"Nhân viên phụ trách"}
              error={!!errors["userInCharge"]}
              message={errors["userInCharge"]?.message}
            />
          )}
        />
        <Controller
          key={"facility"}
          control={control}
          name={"facility"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={facility?.content ? facility?.content : []}
              fullWidth={true}
              loading={isLoadingFacility}
              value={value}
              onChange={onChange}
              label={"Kho lấy hàng"}
              error={!!errors["facility"]}
              message={errors["facility"]?.message}
            />
          )}
        />
      </Stack>
      <Stack direction="row" justifyContent={"space-around"} spacing={5}>
        <Controller
          key={"startedDate"}
          control={control}
          name={"startedDate"}
          render={({ field: { onChange, value } }) => (
            <CustomDatePicker
              value={value}
              error={!!errors["startedDate"]}
              message={errors["startedDate"]?.message}
              onChange={onChange}
              fullWidth={true}
              label={"Ngày bắt đầu"}

              // minDate={minDate}
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
export default CreateTripForm;
