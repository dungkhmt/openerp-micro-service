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
  useCreateProductCategory,
  useGetProductCateList,
} from "../../../controllers/query/category-query";
import { categoryColumns } from "../LocalConstant";
function ProductCategoryScreen({ screenAuthorization }) {
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

  const { isLoading, data } = useGetProductCateList();
  const createProductCategoryQuery = useCreateProductCategory();
  const onSubmit = async (data) => {
    let params = {
      name: data?.name.trim(),
    };
    await createProductCategoryQuery.mutateAsync(params);
    setIsAdd((pre) => !pre);
    reset();
  };
  const fields = [
    {
      name: "name",
      label: "Tên danh mục",
      type: "text",
      component: "input",
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
      <Box>
        <CustomToolBar actions={actions} />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={categoryColumns}
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

const SCR_ID = "SCR_PRODUCT_CATEGORY";
export default withScreenSecurity(ProductCategoryScreen, SCR_ID, true);
