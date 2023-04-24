import AddIcon from "@mui/icons-material/Add";
import { Box, Button, Typography } from "@mui/material";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import CustomFormControl from "../../../components/form/CustomFormControl";
import CustomModal from "../../../components/modal/CustomModal";
import {
  useCreateCustomer,
  useGetContractType,
  useGetCustomerList,
  useGetCustomerType,
} from "../../../controllers/query/category-query";
import { useGetFacilityList } from "../../../controllers/query/facility-query";
import { AppColors } from "../../../shared/AppColors";
import { staticCustomerField } from "../LocalConstant";
function CustomerScreen({ screenAuthorization }) {
  const status = [{ name: "active" }, { name: "inactive" }];
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const { isLoading, data } = useGetCustomerList();
  const [isAdd, setIsAdd] = useToggle(false);
  const [currPos, setCurrPos] = useState({
    latitude: "",
    longitude: "",
  });
  const methods = useForm({
    mode: "onChange",
    defaultValues: {},
    // resolver: brandSchema,
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
  const { isLoading: isLoadingFacility, data: facility } = useGetFacilityList();
  const createCustomerQuery = useCreateCustomer();

  const onSubmit = async (data) => {
    let customerParams = {
      address: data?.address,
      contractTypeCode: data?.contractType?.code,
      customerTypeCode: data?.customerType?.code,
      facilityCode: data?.facility?.code,
      status: data?.status?.name,
      name: data?.name,
      phone: data?.phone,
      latitude: currPos ? currPos?.latitude : "",
      longitude: currPos ? currPos?.longitude : "",
    };
    console.log("Prams:", customerParams);
    await createCustomerQuery.mutateAsync(customerParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  let actions = [
    {
      title: "Thêm",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
    {
      title: "Sửa",
      callback: () => {
        console.log("call back");
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const fields = [
    {
      name: "address",
      label: "Địa chỉ",
      type: "text",
      component: "input",
    },
    {
      name: "name",
      label: "Tên khách hàng",
      type: "text",
      component: "input",
    },
    {
      name: "phone",
      label: "Số điện thoại",
      type: "text",
      component: "input",
    },
    {
      name: "customerType",
      label: "Loại khách hàng",
      component: "select",
      options: customerType ? customerType?.content : [],
      loading: isLoadingCustomerType,
    },
    {
      name: "status",
      label: "Trạng thái",
      options: status,
      loading: false,
      component: "select",
    },
    {
      name: "facility",
      label: "Kho trực thuộc",
      component: "select",
      options: facility ? facility?.content : [],
      loading: isLoadingFacility,
    },
    {
      name: "contractType",
      label: "Loại hợp đồng",
      component: "select",
      options: contractType ? contractType?.content : [],
      loading: isLoadingContractType,
    },
  ];
  useEffect(() => {
    navigator.geolocation.getCurrentPosition((position) => {
      setCurrPos({
        latitude: position?.coords?.latitude,
        longitude: position?.coords?.longitude,
      });
    });
  }, []);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          boxShadow: 3,
          margin: "0px -16px 0 -16px",
          paddingX: 2,
          paddingY: 1,
          position: "sticky",
          backgroundColor: "white",
          zIndex: 1000,
        }}
      >
        <Typography
          id="modal-modal-title"
          variant="h6"
          textTransform="capitalize"
          letterSpacing={1}
          fontSize={18}
          sx={{
            fontFamily: "Open Sans",
            color: AppColors.primary,
            fontWeight: "bold",
          }}
        >
          {"KHÁCH HÀNG"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={staticCustomerField}
        rows={data ? data?.content : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2 }}
      >
        <FormProvider {...methods}>
          {/* <Stack spacing={2}> */}
          <CustomFormControl
            control={control}
            errors={errors}
            fields={fields}
          />
          {/* </Stack> */}
        </FormProvider>
        <Button
          onClick={handleSubmit(onSubmit)}
          variant="contained"
          style={{ marginRight: 20, color: "white" }}
        >
          Submit
        </Button>
        <Button onClick={() => reset()} variant={"outlined"}>
          Reset
        </Button>
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_CUSTOMER";
export default withScreenSecurity(CustomerScreen, SCR_ID, true);
