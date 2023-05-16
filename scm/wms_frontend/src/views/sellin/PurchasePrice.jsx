import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import CustomModal from "components/modal/CustomModal";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import { useMemo, useState } from "react";
import { useForm, useWatch } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import { ORDERS_STATUS } from "shared/AppConstants";
import {
  useGetContractType,
  useGetProductList,
} from "../../controllers/query/category-query";
import { purchaseOrderPrice } from "./LocalConstant";
import CreatePurOrderForm from "./components/CreatePurOrderForm";

function PurchasePriceScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const { height } = useWindowSize();
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      productPrice: [],
    },
    // resolver: yupResolver(purchaseOrderSchema),
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = methods;
  const productPrices = useWatch({
    control,
    name: "prices",
  });

  // const history = useHistory();
  // let { path } = useRouteMatch();
  // const handleButtonClick = (params) => {
  //   history.push(`${path}/purchase-order-detail`, {
  //     order: params,
  //     previous: "purchaseOrderScreen",
  //   });
  // };

  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const { isLoading: isLoadingContract, data: contract } = useGetContractType();

  const extraActions = [
    {
      title: "Xem",
      callback: (item) => {
        // handleButtonClick(item);
      },
      icon: <VisibilityIcon />,
      color: AppColors.green,
    },
    {
      title: "Sửa",
      callback: async (item) => {
        setOpenDrawer();
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        setIsRemove();
        setItemSelected(item);
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
    },
  ];

  const mergedProductContractData = useMemo(() => {
    let mergedProducts = product?.content
      ?.map((pro) => {
        return contract?.content?.map((ctr) => {
          let newPro = { ...pro };
          newPro["contract"] = ctr;
          return newPro;
        });
      })
      .flat();
    return mergedProducts?.map((pro, index) => {
      console.log("Pro:", pro);
      return {
        ...pro,
        key: index,
      };
    });
  }, [product, contract]);
  console.log(mergedProductContractData);
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar actions={[]} containSearch={false} />
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoadingProduct || isLoadingContract}
        totalItem={100}
        columns={[
          purchaseOrderPrice[0],
          purchaseOrderPrice[1],
          purchaseOrderPrice[2],
          // {
          //   field: "priceBeforeVat",
          //   headerName: "Giá trước thuế",
          //   sortable: false,
          //   minWidth: 150,
          //   type: "number",
          //   editable: true,
          //   headerAlign: "center",
          //   align: "center",
          //   flex: 1,
          //   renderCell: (params) => {
          //     const price = productPrices.find((el) => el.id === params.id);
          //     return price ? price.priceBeforeVat : "Nhập giá tiền";
          //   },
          //   renderEditCell: (params) => {
          //     const index = productPrices?.findIndex(
          //       (el) => el.id === params.id
          //     );
          //     const value =
          //       index !== -1 ? productPrices[index].priceBeforeVat : null;
          //     return (
          //       <Controller
          //         name={`products.${index}.quantity`}
          //         control={control}
          //         render={({ field: { onChange } }) => (
          //           <InputBase
          //             inputProps={{ min: 0 }}
          //             sx={{
          //               "& .MuiInputBase-input": {
          //                 textAlign: "right",
          //                 fontSize: 14,
          //                 "&::placeholder": {
          //                   fontSize: 13,
          //                   opacity: 0.7,
          //                   fontStyle: "italic",
          //                 },
          //               },
          //             }}
          //             placeholder="Nhập giá tiền"
          //             value={value}
          //             onChange={onChange}
          //           />
          //         )}
          //       />
          //     );
          //   },
          // },
          {
            field: "action",
            headerName: "Hành động",
            headerAlign: "center",
            align: "center",
            sortable: false,
            width: 125,
            minWidth: 150,
            maxWidth: 200,
            type: "actions",
            getActions: (params) => {
              return [
                ...extraActions.map((extraAction, index) => (
                  <Action
                    item={params.row}
                    key={index}
                    extraAction={extraAction}
                    onActionCall={extraAction.callback}
                    disabled={params?.row?.status !== ORDERS_STATUS.created}
                  />
                )),
              ];
            },
          },
        ]}
        // rows={data ? data?.content : []}
        rows={mergedProductContractData ? mergedProductContractData : []}
      />
      <CustomModal
        open={isAdd}
        toggle={setIsAdd}
        size="sm"
        title="Tạo mới đơn mua hàng"
      >
        <CreatePurOrderForm setIsAdd={setIsAdd} />
      </CustomModal>

      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal onClose={setOpenDrawer} title="Sửa thông tin đơn hàng" />
        {/* <UpdateProductForm /> */}
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={(flag) => {}}
      />
    </Box>
  );
}

const SCR_ID = "SCR_PURCHASE_PRICE";
export default withScreenSecurity(PurchasePriceScreen, SCR_ID, true);
