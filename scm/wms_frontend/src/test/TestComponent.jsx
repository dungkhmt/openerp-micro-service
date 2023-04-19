import { Box, Typography } from "@mui/material";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import CustomDataGrid from "../components/datagrid/CustomDataGrid";
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
  return (
    <Box>
      <Typography>Test component</Typography>
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
      <CustomDataGrid
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
    </Box>
  );
};
export default TestComponent;
