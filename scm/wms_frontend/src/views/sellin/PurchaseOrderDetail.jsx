import { KeyboardArrowLeft, KeyboardArrowRight } from "@mui/icons-material";
import AddIcon from "@mui/icons-material/Add";
import {
  Box,
  Button,
  Divider,
  IconButton,
  InputBase,
  Typography,
} from "@mui/material";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import CustomModal from "components/modal/CustomModal";
import CustomBillTable from "components/table/CustomBillTable";
import CustomOrderTable from "components/table/CustomOrderTable";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useCreateReceiptBill,
  useGetBillItemOfPurchaseOrder,
} from "controllers/query/bill-query";
import { useGetProductList } from "controllers/query/category-query";
import { useGetPurchaseOrderItems } from "controllers/query/purchase-order-query";
import moment from "moment";
import { useCallback, useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { useToggle, useWindowSize } from "react-use";
import { CustomDatePicker } from "../../components/datepicker/CustomDatePicker";

function PurchaseOrderDetailScreen({}) {
  const location = useLocation();
  const currOrder = location.state.order;
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const createBillQuery = useCreateReceiptBill();
  const [isAdd, setIsAdd] = useToggle(false);
  const [showTable1, setShowTable1] = useState(true);

  const toggleTables = () => {
    setShowTable1((prev) => !prev);
  };
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
  const onSubmit = async (data) => {
    let importParams = {
      orderCode: currOrder?.code,
      importItems: data?.products?.map((pro) => {
        return {
          expireDate: moment(pro?.expired_date).format("YYYY-MM-DD"),
          effectQty: pro?.quantity,
          productCode: pro?.code,
        };
      }),
    };
    await createBillQuery.mutateAsync(importParams);
    setIsAdd((pre) => !pre);
    reset();
  };
  let actions = [
    {
      title: "Tạo phiếu nhập kho",
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
  var product_fields = [
    {
      field: "code",
      headerName: "Mã code",
      sortable: false,
      pinnable: true,
    },
    {
      field: "name",
      headerName: "Tên sản phẩm",
      sortable: false,
      minWidth: 150,
    },
    {
      field: "status",
      headerName: "Trạng thái",
      sortable: false,
      minWidth: 100,
    },
    {
      field: "quantity",
      headerName: "Số lượng mua",
      sortable: false,
      minWidth: 150,
      type: "number",
      editable: true,
      renderCell: (params) => {
        const product = products.find((el) => el.id === params.id);
        return product ? product.quantity : "Nhập số lượng";
      },
      renderEditCell: (params) => {
        const index = products?.findIndex((el) => el.id === params.id);
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
    {
      field: "expireDate",
      headerName: "Ngày hết hạn",
      sortable: false,
      minWidth: 150,
      type: "date",
      editable: true,
      renderCell: (params) => {
        const product = products.find((el) => el.id === params.id);
        return product
          ? moment(product?.expired_date).format("DD-MM-YYYY")
          : "Nhập ngày hết hạn";
      },
      renderEditCell: (params) => {
        const index = products?.findIndex((el) => el.id === params.id);
        const value = index !== -1 ? products[index].expired_date : null;
        return (
          <Controller
            name={`products.${index}.expired_date`}
            control={control}
            render={({ field: { onChange } }) => (
              <CustomDatePicker value={value} onChange={onChange} />
            )}
          />
        );
      },
    },
  ];
  const { isLoading, data: orderItem } = useGetPurchaseOrderItems({
    orderCode: currOrder?.code,
  });
  const { isLoadingBillItem, data: billItem } = useGetBillItemOfPurchaseOrder({
    orderCode: currOrder?.code,
  });
  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const renderCustomTable = useCallback(() => {
    return (
      <CustomOrderTable items={orderItem?.content ? orderItem?.content : []} />
    );
  }, [orderItem]);
  const renderCustomBill = useCallback(() => {
    return (
      <CustomBillTable orderItem={orderItem?.content} billItem={billItem} />
    );
  }, [orderItem, billItem]);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <Box>
        <Typography>Thông tin cơ bản</Typography>
        <Typography></Typography>
        <Typography>1. Mã đơn: {currOrder?.code}</Typography>
        <Typography>2. Nhà sản xuất: {currOrder?.supplierCode}</Typography>
        <Typography>3. Người tạo: {currOrder?.user?.id}</Typography>
        <Typography>4. Thời gian tạo: {currOrder?.createdDate}</Typography>
        <Typography>5. Kho trực thuộc: {currOrder?.facility?.name}</Typography>
      </Box>
      {renderCustomTable()}
      <Divider variant="fullWidth" sx={{ marginTop: 2, height: 5 }} />
      <Box>
        <Box flexDirection={"row"}>
          <Typography>Trạng thái</Typography>
          <Button variant="outlined">{currOrder?.status}</Button>
        </Box>
      </Box>
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        style={{ padding: 2 }}
      >
        {renderCustomBill()}
        <IconButton onClick={toggleTables}>
          {showTable1 ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
        </IconButton>
        <FormProvider {...methods}>
          <CustomDataGrid
            isSelectable={true}
            params={params}
            setParams={setParams}
            sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
            // isLoading={}
            totalItem={100}
            columns={product_fields}
            rows={
              product?.content
                ? product?.content?.filter((pro) =>
                    orderItem?.content
                      ?.map((item) => item?.product?.code)
                      .includes(pro?.code)
                  )
                : []
            }
            onSelectionChange={(ids) => {
              let results = product?.content.filter((pro) =>
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

const SCR_ID = "SCR_PURCHASE_ORDER_DETAIL";
export default withScreenSecurity(PurchaseOrderDetailScreen, SCR_ID, true);
