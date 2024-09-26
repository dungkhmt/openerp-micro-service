import { Button, Stack } from "@mui/material";
import { CustomDatePicker } from "components/datepicker/CustomDatePicker";
import CustomSelect from "components/select/CustomSelect";
import { useCreateDeliveryTrip } from "controllers/query/delivery-trip-query";
import { useGetFacilityListNoPaging } from "controllers/query/facility-query";
import { useGetAllUsersExist } from "controllers/query/user-query";
import moment from "moment";

const { FormProvider, useForm, Controller } = require("react-hook-form");

const CreateTripForm = ({ setIsAdd, currShipment }) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: yupResolver(tripSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const { isLoading: isLoadingUser, data: userInCharge } =
    useGetAllUsersExist();
  const { isLoading: isLoadingFacility, data: facility } =
    useGetFacilityListNoPaging();

  const createTripQuery = useCreateDeliveryTrip();

  const onSubmit = async (data) => {
    let tripParams = {
      startedDate: moment(data?.startedDate).format("DD-MM-YYYY"),
      shipmentCode: currShipment?.code,
      userInCharge: data?.userInCharge?.id,
      facilityCode: data?.facility?.code,
    };
    await createTripQuery.mutateAsync(tripParams);
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
        {/* <Typography>
          Nên có thêm phần gợi ý xem lấy kho nào thì trùng với cụm customers
          đang được phân trip
        </Typography> */}
        <Controller
          key={"userInCharge"}
          control={control}
          name={"userInCharge"}
          render={({ field: { onChange, value } }) => (
            <CustomSelect
              readOnly={false}
              options={
                userInCharge
                  ? userInCharge
                      ?.filter(
                        (user) => user.registeredRoles === "SCM_DELIVERY_STAFF"
                      )
                      .map((user) => {
                        return {
                          name:
                            user?.firstName +
                            " " +
                            user?.middleName +
                            " " +
                            user?.lastName,
                          id: user?.id,
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
              options={facility ? facility : []}
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
