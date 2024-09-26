import { Box } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
const data = [
  {
    id: 1,
    zone: "kfdjf",
    distributor_id: 1,
  },
  {
    id: 2,
    zone: "kfdjf",
    distributor_id: 2,
  },
];
const TestComponent = () => {
  const [value, setValue] = useState(0);
  function a11yProps(index) {
    return {
      id: `simple-tab-${index}`,
      "aria-controls": `simple-tabpanel-${index}`,
    };
  }
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };
  const [isDragDialogOpen, setOpenDragDialog] = useToggle(false);
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();
  const [isAdd, setIsAdd] = useToggle(false);
  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* <CustomMap /> */}
      {/* <Typography>Test component</Typography> */}
      {/* <FacebookCircularProgress />
      <ErrorDialog open={true} />
      <NotAuthorized />
      <PrimaryButton>
        <Typography>djfkdj</Typography>
      </PrimaryButton>
      <TertiaryButton>
        <Typography>djfkdj</Typography>
      </TertiaryButton> */}
      {/* <DraggableDialog
        title="Duyệt khách hàng"
        message="Bạn chắc chắn muốn duyệt vị trí khách hàng này"
        disable={false}
        open={false}
        handleOpen={setOpenDragDialog}
        callback={(flag) => {
          console.log("Flag: ", flag);
        }}
      />
      <FooterModal
        onCancel={() => {
          console.log("Canceled!");
        }}
        disabled={false}
        loading={false}
        onSubmit={() => {
          console.log("Submitting");
        }}
        style={{
          mx: -2,
        }}
      />
      <HeaderModal
        title="kfjdkfj"
        onClose={() => {
          console.log("Closing!");
        }}
      />
      <CustomModal open={false} size="md">
        <>
          <HeaderModal title="Thêm mùi hương" />
          <Typography>djfkdjf</Typography>
          <FooterModal
          // onSubmit={handleSubmit((data) => {
          //   mutateAsync({ ...data });
          // })}
          // disabled={!isValid || isLoading}
          // onCancel={toggle}
          />
        </>
      </CustomModal> */}
      {/* <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 56 - 64 - 200 - 8 }}
        isLoading={false}
        totalItem={100}
        columns={[
          {
            field: "zone",
            headerName: "Miền",
            sortable: false,
            pinnable: true,
          },
          {
            field: "distributor_id",
            headerName: "Mã NPP",
            sortable: false,
            minWidth: 150,
            // valueGetter: (params) =>
            //   params.row.order.customer_buy.distributor.code,
          },
        ]}
        rows={data ? data : []}
      />
      <CustomDrawer open={isAdd}>
        <Typography>dkjdkjf</Typography>
      </CustomDrawer> */}
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <DatePicker />
      </LocalizationProvider>
    </Box>
  );
};
export default TestComponent;
