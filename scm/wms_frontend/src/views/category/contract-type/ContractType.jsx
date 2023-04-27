import AddIcon from "@mui/icons-material/Add";
import { Box, Button, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import CustomFormControl from "../../../components/form/CustomFormControl";
import CustomModal from "../../../components/modal/CustomModal";
import {
  useCreateContractType,
  useGetContractType,
  useGetDistChannelList,
} from "../../../controllers/query/category-query";
function ContractTypeScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
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

  const { isLoading, data } = useGetContractType();
  const { isLoadingDistChannel, data: distChannel } = useGetDistChannelList();
  const createContractTypeQuery = useCreateContractType();
  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
      channelCode: data?.channelCode?.code,
    };
    await createContractTypeQuery.mutateAsync(params);
    setIsAdd((pre) => !pre);
    reset();
  };
  const fields = [
    {
      name: "name",
      label: "Loại hợp đồng",
      type: "text",
      component: "input",
    },
    {
      name: "channelCode",
      label: "Kênh phân phối",
      component: "select",
      options: distChannel ? distChannel?.content : [],
      loading: isLoadingDistChannel,
    },
  ];
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
          color={green[800]}
          fontSize={17}
        >
          {"HỢP ĐỒNG GIAO DỊCH"}
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
        columns={[
          {
            field: "code",
            headerName: "Mã code",
            sortable: false,
            pinnable: true,
            minWidth: 200,
          },
          {
            field: "name",
            headerName: "Tên hợp đồng giao dịch",
            sortable: false,
            minWidth: 200,
          },
          {
            field: "channel",
            headerName: "Kênh phân phối",
            sortable: false,
            minWidth: 200,
            valueGetter: (params) => {
              return params?.row?.channel?.name;
            },
          },
          {
            field: "",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
          },
        ]}
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

const SCR_ID = "SCR_CONTRACT_TYPE";
export default withScreenSecurity(ContractTypeScreen, SCR_ID, true);
