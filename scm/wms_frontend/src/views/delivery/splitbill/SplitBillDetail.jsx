import AddIcon from "@mui/icons-material/Add";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Stack, Typography } from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetBillItemsOfBill,
  useGetSplittedBillItem,
} from "controllers/query/bill-query";
import { useGetDeliveryTripToAssignBill } from "controllers/query/delivery-trip-query";
import { useCallback, useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { Action } from "../../../components/action/Action";
import CustomSelect from "../../../components/select/CustomSelect";
import CustomSplitBillTable from "../../../components/table/CustomSplitBillTable";
import { useAssignShipmentToTrip } from "../../../controllers/query/shipment-query";
import { splittedBillCols } from "../LocalConstant";
import SplitBillForm from "./components/SplitBillForm";
function SplitBillDetailScreen({ screenAuthorization }) {
  const location = useLocation();
  const currBills = location.state.bills;
  const [isAdd, setIsAdd] = useToggle(false);
  const [isAddTrip, setIsAddTrip] = useToggle(false);
  const [currShipmentItem, setCurrShipmentItem] = useState("");
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      products: [],
    },
    // resolver: brandSchema,
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = methods;
  const { height } = useWindowSize();
  const { isLoading: isLoadingBillItem, data: deliveryBillItems } =
    useGetBillItemsOfBill({
      bill_code: currBills?.code,
    });
  const { isLoading: isLoadingSplittedBillItem, data: splittedBillItems } =
    useGetSplittedBillItem({
      deliveryBillCode: currBills?.code,
    });
  const { isLoading: isLoadingTrip, data: trips } =
    useGetDeliveryTripToAssignBill({
      billCode: currBills?.code,
    });

  const assignBillToTripQuery = useAssignShipmentToTrip();
  const renderCustomTable = useCallback(() => {
    return (
      <CustomSplitBillTable
        items={deliveryBillItems ? deliveryBillItems : []}
      />
    );
  }, []);
  let actions = [
    {
      title: "Chia đơn",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Xem chi tiết",
      callback: (item) => {},
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Thêm đơn vào chuyến",
      callback: (item) => {
        setCurrShipmentItem(item);
        setIsAddTrip((pre) => !pre);
      },
      icon: <AddIcon />,
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} containSearch={false} />
      </Box>
      <Box>
        <Stack direction={"row"} spacing={2} alignItems={"center"} marginY={2}>
          <Stack
            sx={{
              borderRadius: 50,
              background: "gray",
              width: 30,
              height: 30,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography sx={{ color: "white" }}>1</Typography>
          </Stack>
          <Typography
            sx={{
              color: AppColors.secondary,
              fontSize: 18,
              fontWeight: "600",
            }}
          >
            THÔNG TIN CƠ BẢN
          </Typography>
        </Stack>
        <Typography></Typography>
        <Typography>1. Mã đơn : {currBills?.code}</Typography>
        <Typography>2. Mã order: {currBills?.saleOrder?.code}</Typography>
        <Typography>3. Tổng số item: {deliveryBillItems?.length}</Typography>
      </Box>
      <Stack
        direction={"row"}
        alignItems={"center"}
        justifyContent={"center"}
        sx={{ marginTop: 2 }}
      >
        {renderCustomTable()}
      </Stack>
      <Stack
        direction={"row"}
        spacing={2}
        alignItems={"center"}
        sx={{ marginY: 2 }}
      >
        <Stack
          sx={{
            borderRadius: 50,
            background: "gray",
            width: 30,
            height: 30,
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Typography sx={{ color: "white" }}>2</Typography>
        </Stack>
        <Typography
          sx={{
            color: AppColors.secondary,
            fontSize: 18,
            fontWeight: "600",
          }}
        >
          THÔNG TIN ĐƠN ĐÃ CHIA
        </Typography>
      </Stack>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoadingSplittedBillItem}
        totalItem={100}
        columns={[
          ...splittedBillCols,
          {
            field: "actions",
            headerName: "Hành động",
            sortable: false,
            flex: 1,
            type: "actions",
            getActions: (params) => {
              return [
                ...extraActions.map((extraAction, index) => {
                  return (
                    <Action
                      item={params.row}
                      key={index}
                      extraAction={extraAction}
                      onActionCall={extraAction.callback}
                      disabled={params?.row?.deliveryTrip != null}
                    />
                  );
                }),
              ];
            },
          },
        ]}
        rows={splittedBillItems ? splittedBillItems : []}
      />
      <CustomModal open={isAdd} toggle={setIsAdd} size="sm" title="Chia đơn">
        <SplitBillForm
          currBills={currBills}
          deliveryBillItems={deliveryBillItems}
          setIsAdd={setIsAdd}
          splittedBillItems={splittedBillItems}
        />
      </CustomModal>
      <CustomModal
        open={isAddTrip}
        toggle={setIsAddTrip}
        size="sm"
        style={{ padding: 2, zIndex: 3 }}
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
          {"THÊM ĐƠN VÀO CHUYẾN"}
        </Typography>
        <FormProvider {...methods}>
          <Controller
            control={control}
            name={"trips"}
            key={"select"}
            render={({ field: { onChange, value } }) => (
              <CustomSelect
                options={
                  trips
                    ? trips.map((trip) => {
                        return {
                          name: trip?.code + "-" + trip?.userInCharge?.id,
                        };
                      })
                    : []
                }
                loading={isLoadingTrip}
                value={value}
                label={"Trips"}
                onChange={onChange}
              />
            )}
          />
        </FormProvider>
        <Button
          onClick={handleSubmit(async (data) => {
            let tripCode = data?.trips?.name.split("-")[0];
            let assignParams = {
              shipmentItemCode: currShipmentItem?.code,
              tripCode: tripCode,
            };
            await assignBillToTripQuery.mutateAsync(assignParams);
            setIsAddTrip((pre) => !pre);
          })}
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

const SCR_ID = "SCR_SPLIT_BILL_DETAIL";
export default withScreenSecurity(SplitBillDetailScreen, SCR_ID, true);
