import AddIcon from "@mui/icons-material/Add";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Button, Stack, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomSelect from "components/select/CustomSelect";
import CustomSplitBillTable from "components/table/CustomSplitBillTable";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetBillItemsOfBill,
  useGetSplittedBillItem,
} from "controllers/query/bill-query";
import { useGetDeliveryTripToAssignBill } from "controllers/query/delivery-trip-query";
import { useAssignShipmentToTrip } from "controllers/query/shipment-query";
import { useCallback, useMemo, useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { convertUserToName } from "../../../utils/GlobalUtils";
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
    pageSize: 10,
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
  }, [deliveryBillItems]);

  const isSplittable = (deliveryBillItems, splittedBillItems) => {
    let newdeliveryBillItems = deliveryBillItems?.map((item) => {
      let filteredSplittedItem = splittedBillItems?.filter(
        (i) => i?.deliveryBillItemSeqId === item?.seqId
      );
      let splittedQty = filteredSplittedItem?.reduce(
        (pre, curr) => pre + curr.quantity,
        0
      );

      return {
        ...item,
        splittedQty: splittedQty,
      };
    });
    return !newdeliveryBillItems?.reduce((accumulator, curr) => {
      return accumulator && curr.effectiveQty === curr.splittedQty;
    }, true);
  };
  /**
   * This code is only used to get product field for splittedBillItem
   * // start
   */
  const addProductToBillItem = (splittedBillItem, deliveryBillItems) => {
    let newSplittedItem = [];
    splittedBillItem?.forEach((item) => {
      deliveryBillItems?.forEach((ditem) => {
        if (ditem?.seqId === item?.deliveryBillItemSeqId) {
          item["product"] = ditem?.product;
          newSplittedItem = [...newSplittedItem, item];
        }
      });
    });
    return newSplittedItem;
  };
  const newSplittedItem = useMemo(() => {
    addProductToBillItem(splittedBillItems, deliveryBillItems);
  }, [splittedBillItems, deliveryBillItems]);
  /**
   * This code is only used to get product field for splittedBillItem
   * // end
   */
  let actions = [
    {
      title: "Chia đơn",
      callback: (pre) => {
        setIsAdd((pre) => !pre);
      },
      icon: <AddIcon />,
      describe: "Thêm bản ghi mới",
      disabled: !isSplittable(deliveryBillItems, splittedBillItems),
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
        <CustomToolBar
          actions={actions}
          containSearch={false}
          containFilter={false}
        />
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
        <Stack sx={{ flexDirection: "row", marginTop: 2 }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            1. Mã đơn:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currBills?.code}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            2. Mã order:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {currBills?.saleOrder?.code}
          </Typography>
        </Stack>
        <Stack sx={{ flexDirection: "row" }}>
          <Typography
            sx={{
              fontSize: 16,
              color: AppColors.green,
            }}
          >
            3. Tổng số item:
          </Typography>
          <Typography
            sx={{
              marginLeft: 2,
              fontSize: 16,
              fontWeight: "bold",
              color: AppColors.secondary,
            }}
          >
            {deliveryBillItems?.length}
          </Typography>
        </Stack>
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
        totalItem={splittedBillItems?.totalElements}
        handlePaginationModelChange={(props) => {
          setParams({
            page: props?.page + 1,
            pageSize: props?.pageSize,
          });
        }}
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
        title="Thêm đơn vào chuyến"
      >
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
                        console.log("Trip: ", trip);
                        return {
                          name:
                            trip?.code +
                            "-" +
                            convertUserToName(trip?.userInCharge),
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
        <Stack
          sx={{ marginY: 2 }}
          direction={"row"}
          spacing={2}
          justifyContent={"flex-end"}
        >
          <Button onClick={() => reset()} variant={"outlined"}>
            Reset
          </Button>
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
            style={{ color: "white" }}
          >
            Submit
          </Button>
        </Stack>
      </CustomModal>
    </Box>
  );
}

const SCR_ID = "SCR_SCM_SPLIT_BILL_DETAIL";
export default withScreenSecurity(SplitBillDetailScreen, SCR_ID, true);
