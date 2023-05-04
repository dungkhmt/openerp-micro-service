import AddIcon from "@mui/icons-material/Add";
import { Box, Button } from "@mui/material";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import CustomDataGrid from "../../../components/datagrid/CustomDataGrid";
import CustomFormControl from "../../../components/form/CustomFormControl";
import CustomModal from "../../../components/modal/CustomModal";
import {
  useCreateProduct,
  useGetProductCateList,
  useGetProductList,
  useGetProductUnitList,
} from "../../../controllers/query/category-query";
var columns = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 200,
  },
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    minWidth: 200,
  },
  {
    field: "brand",
    headerName: "Nhãn hiệu",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "sku",
    headerName: "Mã SKU",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "product_category_name",
    headerName: "Loại sản phẩm",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params.row.productCategory.name;
    },
  },
  {
    field: "product_unit_name",
    headerName: "Đơn vị tính",
    sortable: false,
    minWidth: 190,
    valueGetter: (params) => {
      return params.row.productUnit.name;
    },
  },
  {
    field: "unitPerBox",
    headerName: "Số lượng/đơn vị",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "",
    headerName: "Hành động",
    sortable: false,
    minWidth: 100,
  },
];

function ProductScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 5,
  });
  const { height } = useWindowSize();
  const { isLoading, data, isRefetching, isPreviousData } = useGetProductList();
  const { isLoading: isLoadingProductCate, data: productCate } =
    useGetProductCateList();
  const { isLoading: isLoadingProductUnit, data: productUnit } =
    useGetProductUnitList();
  const status = [{ name: "active" }, { name: "inactive" }];
  const createProductQuery = useCreateProduct();
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
  const onSubmit = async (data) => {
    let productParams = {
      brand: data?.brand,
      categoryId: data?.categoryId?.id,
      massType: "oke",
      name: data?.name,
      sku: data?.sku,
      status: data?.status?.name,
      unitId: data?.unitId?.id,
      unitPerBox: data?.unitPerBox,
    };
    const res = await createProductQuery.mutateAsync(productParams);
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
      name: "name",
      label: "Tên sản phẩm",
      type: "text",
      component: "input",
    },
    {
      name: "sku",
      label: "Mã sku",
      type: "text",
      component: "input",
    },
    {
      name: "brand",
      label: "Nhãn hiệu",
      type: "text",
      component: "input",
    },
    {
      name: "categoryId",
      label: "Ngành hàng",
      component: "select",
      options: productCate ? productCate?.content : [],
      loading: isLoadingProductCate,
    },
    {
      name: "status",
      label: "Trạng thái",
      options: status,
      loading: false,
      component: "select",
    },
    {
      name: "unitId",
      label: "Đơn vị tính",
      component: "select",
      options: productUnit ? productUnit?.content : [],
      loading: isLoadingProductUnit,
    },
    {
      name: "unitPerBox",
      label: "Số lượng/đơn vị",
      type: "text",
      component: "input",
    },
  ];
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        isSerial
        // initialState={{
        //   pagination: {
        //     paginationModel: { pageSize: 5, page: 0 },
        //   },
        // }}
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
        isLoading={isLoading || isRefetching || isPreviousData}
        totalItem={100}
        columns={columns}
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

const SCR_ID = "SCR_PRODUCT";
export default withScreenSecurity(ProductScreen, SCR_ID, true);
