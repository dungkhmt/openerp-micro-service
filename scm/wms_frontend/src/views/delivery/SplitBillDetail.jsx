import AddIcon from "@mui/icons-material/Add";
import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Box,
  Button,
  InputBase,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateSplitBillItem,
  useGetBillItemsOfBill,
  useGetSplittedBillItem,
} from "controllers/query/bill-query";
import { useGetDeliveryTripList } from "controllers/query/delivery-trip-query";
import { useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import CustomSelect from "../../components/select/CustomSelect";
import { useAssignShipmentToTrip } from "../../controllers/query/shipment-query";
import { Action } from "../sellin/PurchaseOrder";
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
  const products = useWatch({
    control,
    name: "products",
  });
  const { height } = useWindowSize();
  const { isLoading: isLoadingBillItem, data: deliveryBillItems } =
    useGetBillItemsOfBill({
      bill_code: currBills?.code,
    });
  const { isLoading: isLoadingSplittedBillItem, data: splittedBillItems } =
    useGetSplittedBillItem({
      deliveryBillCode: currBills?.code,
    });
  const { isLoading: isLoadingTrip, data: trips } = useGetDeliveryTripList();

  const createSplitBillQuery = useCreateSplitBillItem();
  const assignBillToTripQuery = useAssignShipmentToTrip();
  const onSubmit = async (data) => {
    let splitParams = {
      billItemDTOS: data?.products?.map((pro) => {
        return {
          quantity: pro?.quantity,
          deliveryBillItemSeqId: pro?.seqId,
          productCode: pro?.product?.code,
        };
      }),
      deliveryBillCode: currBills?.code,
    };
    // TODO: Why this do not render data updated
    await createSplitBillQuery.mutateAsync(splitParams);
    setIsAdd((pre) => !pre);
    reset();
  };

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
        console.log("Item: ", item);
        setCurrShipmentItem(item);
        setIsAddTrip((pre) => !pre);
      },
      icon: <AddIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
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
          fontSize={18}
          sx={{
            fontFamily: "Open Sans",
            color: AppColors.primary,
            fontWeight: "bold",
          }}
        >
          {"CHI TIẾT ĐƠN"}
        </Typography>
      </Box>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <Box>
        <Typography>Thông tin cơ bản</Typography>
        <Typography></Typography>
        <Typography>1. Mã đơn : {currBills?.code}</Typography>
        <Typography>2. Mã order: {currBills?.saleOrder?.code}</Typography>
        <Typography>3. Tổng số item: {deliveryBillItems?.length}</Typography>
      </Box>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead>
            <TableRow>
              {/* <TableCell>Mã bill</TableCell> */}
              <TableCell align="right">Tên sản phẩm</TableCell>
              <TableCell align="right">Lượng thực xuất</TableCell>
              <TableCell align="right">Seq id</TableCell>
              {/* <TableCell align="right">Tổng cộng</TableCell> */}
            </TableRow>
          </TableHead>
          <TableBody>
            {deliveryBillItems?.map((row) => (
              <TableRow
                key={row.name}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                {/* <TableCell component="th" scope="row">
                {row.code}
              </TableCell> */}
                <TableCell align="right">{row?.product?.name}</TableCell>
                <TableCell align="right">{row.effectiveQty}</TableCell>
                <TableCell align="right">{row.seqId}</TableCell>
                {/* <TableCell align="right">{row.total_money}</TableCell> */}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
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
        {"ĐƠN ĐÃ CHIA"}
      </Typography>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoadingSplittedBillItem}
        totalItem={100}
        columns={[
          {
            field: "code",
            headerName: "Mã code",
            sortable: false,
            pinnable: true,
            minWidth: 150,
          },
          {
            field: "quantity",
            headerName: "Số lượng chia",
            sortable: false,
            pinnable: true,
            minWidth: 150,
          },
          {
            field: "deliveryBillItemSeqId",
            headerName: "Seq id",
            sortable: false,
            pinnable: true,
            minWidth: 150,
          },
          {
            field: "deliveryBillCode",
            headerName: "Đơn gốc",
            sortable: false,
            pinnable: true,
            minWidth: 150,
            valueGetter: (params) => {
              return params?.row?.deliveryBill?.code;
            },
          },
          {
            field: "actions",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
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
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2 }}
      >
        {/* {renderCustomBill()} */}
        {/* <IconButton onClick={toggleTables}>
          {showTable1 ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
        </IconButton> */}
        <FormProvider {...methods}>
          <CustomDataGrid
            isSelectable={true}
            params={params}
            setParams={setParams}
            sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
            // isLoading={}
            totalItem={100}
            columns={[
              {
                field: "productName",
                headerName: "Tên sản phẩm",
                sortable: false,
                pinnable: true,
                minWidth: 150,
                valueGetter: (params) => {
                  return params?.row?.product?.name;
                },
              },
              {
                field: "formerQty",
                headerName: "Số lượng ban đầu",
                sortable: false,
                pinnable: true,
                minWidth: 150,
                valueGetter: (params) => {
                  return params?.row?.effectiveQty;
                },
              },
              {
                field: "splittedQty",
                headerName: "Số lượng đã chia",
                sortable: false,
                pinnable: true,
                minWidth: 150,
                valueGetter: (params) => {
                  return params?.row?.splittedQty;
                },
              },
              {
                field: "splitQty",
                headerName: "Số lượng định chia",
                sortable: false,
                type: "number",
                editable: true,
                minWidth: 150,
                renderCell: (params) => {
                  const product = products.find((el) => el?.id === params.id);
                  return product ? product.quantity : "Nhập số lượng";
                },
                renderEditCell: (params) => {
                  const index = products?.findIndex(
                    (el) => el.id === params.id
                  );
                  const value = index !== -1 ? products[index].quantity : null;
                  return (
                    <Controller
                      name={`products.${index}.quantity`}
                      control={control}
                      render={({ field: { onChange } }) => (
                        <InputBase
                          inputProps={{ min: 0 }}
                          sx={{
                            "& .MuiInputBase-input": {
                              textAlign: "right",
                              fontSize: 14,
                              "&::placeholder": {
                                fontSize: 13,
                                opacity: 0.7,
                                fontStyle: "italic",
                              },
                            },
                          }}
                          placeholder="Nhập số lượng"
                          value={value}
                          onChange={onChange}
                        />
                      )}
                    />
                  );
                },
              },
            ]}
            rows={deliveryBillItems?.map((item) => {
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
            })}
            onSelectionChange={(ids) => {
              let results = deliveryBillItems?.filter((pro) =>
                ids.includes(pro?.id)
              );
              setValue("products", results);
            }}
          />
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
                  trips.content
                    ? trips.content.map((trip) => {
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
