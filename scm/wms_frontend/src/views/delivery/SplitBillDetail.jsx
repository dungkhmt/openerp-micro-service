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
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomModal from "../../components/modal/CustomModal";
import {
  useCreateSplitBillItem,
  useGetBillItemsOfBill,
  useGetSplittedBillItem,
} from "../../controllers/query/bill-query";
import { AppColors } from "../../shared/AppColors";
import { Action } from "../sellin/PurchaseOrder";
function SplitBillDetailScreen({ screenAuthorization }) {
  const location = useLocation();
  const currBills = location.state.bills;
  const [isAdd, setIsAdd] = useToggle(false);
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
  const createSplitBillQuery = useCreateSplitBillItem();

  const onSubmit = async (data) => {
    let splitParams = {
      billItemDTOS: data?.products?.map((pro) => {
        return {
          quantity: pro?.quantity,
          deliveryBillItemSeqId: pro?.seqId,
        };
      }),
      deliveryBillCode: currBills?.code,
    };
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
      callback: (item) => {
        console.log("item: ", item);
      },
      icon: <VisibilityIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Thêm đơn",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
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
      <Typography>
        Thêm 1 bảng hiển thị các đơn đã chia từ bill này (lấy từ
        export_inventory), bảng này có thể chọn nhiều và - có nút để add vào đơn
        giao hàng chính thức - có nút để add vào 1 trip cụ thể
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
            field: "product",
            headerName: "Tên sản phẩm",
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
            field: "actions",
            headerName: "Hành động",
            sortable: false,
            minWidth: 200,
            type: "actions",
            getActions: (params) => [
              ...extraActions.map((extraAction, index) => (
                <Action
                  item={params.row}
                  key={index}
                  extraAction={extraAction}
                  onActionCall={extraAction.callback}
                  disabled={false}
                />
              )),
            ],
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
              let index = splittedBillItems?.findIndex(
                (i) => i?.deliveryBillItemSeqId === item?.seqId
              );
              let splittedQty = 0;
              if (index != -1) {
                splittedQty = splittedBillItems?.[index]?.quantity;
              }
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
    </Box>
  );
}

const SCR_ID = "SCR_SPLIT_BILL_DETAIL";
export default withScreenSecurity(SplitBillDetailScreen, SCR_ID, true);
