import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import UpdateIcon from "@mui/icons-material/Update";
import { Box, InputBase, Typography } from "@mui/material";
import { Action } from "components/action/Action";
import withScreenSecurity from "components/common/withScreenSecurity";
import CustomDataGrid from "components/datagrid/CustomDataGrid";
import DraggableDeleteDialog from "components/dialog/DraggableDialogs";
import CustomDrawer from "components/drawer/CustomDrawer";
import HeaderModal from "components/modal/HeaderModal";
import CustomToolBar from "components/toolbar/CustomToolBar";
import {
  useGetContractType,
  useGetProductList,
} from "controllers/query/category-query";
import { useGetSellinPrice } from "controllers/query/purchase-order-query";
import { useCallback, useMemo, useState } from "react";
import { Controller, FormProvider, useForm, useWatch } from "react-hook-form";
import { useToggle, useWindowSize } from "react-use";
import { AppColors } from "shared/AppColors";
import {
  useCreateSelloutPrice,
  useDeleteSelloutPrice,
  useGetSelloutPrice,
} from "../../controllers/query/sale-order-query";
import { formatVietnameseCurrency } from "../../utils/GlobalUtils";
import { saleOrderPrices } from "./LocalConstant";
import UpdateSalePrice from "./components/UpdateSalePrice";

function SalePriceScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    pageSize: 10,
  });
  const [isAdd, setIsAdd] = useToggle(false);
  const [isRemove, setIsRemove] = useToggle(false);
  const [itemSelected, setItemSelected] = useState(null);
  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const { height } = useWindowSize();
  const methods = useForm({
    mode: "onChange",
    defaultValues: {
      productSalePrices: [],
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
  const productSalePrices = useWatch({
    control,
    name: "productSalePrices",
  });

  const { isLoading: isLoadingProduct, data: product } = useGetProductList();
  const { isLoading: isLoadingContract, data: contract } = useGetContractType();
  const { isLoading: isLoadingSellinPrice, data: sellinPrices } =
    useGetSellinPrice();
  const { isLoading: isLoadingSelloutPrice, data: selloutPrices } =
    useGetSelloutPrice();
  const createSalePrices = useCreateSelloutPrice();
  const deleteSalePrices = useDeleteSelloutPrice({
    id: itemSelected?.id,
  });
  const onSubmit = async (data) => {
    let productSalePriceParams = data?.productSalePrices.map((pro) => {
      return {
        contractDiscount: pro?.contractDiscount,
        contractTypeCode: pro?.contract?.code,
        massDiscount: pro?.massDiscount,
        productCode: pro?.productEntity?.code,
      };
    });
    if (productSalePriceParams.length > 0)
      await createSalePrices.mutateAsync(productSalePriceParams);
    reset();
  };

  let actions = [
    {
      title: "Cập nhật",
      callback: handleSubmit(onSubmit),
      icon: <UpdateIcon />,
      describe: "Thêm bản ghi mới",
      disabled: false,
    },
  ];
  const extraActions = [
    {
      title: "Sửa",
      callback: async (item) => {
        setOpenDrawer((pre) => !pre);
        setItemSelected(
          selloutPrices?.find(
            (el) =>
              el?.productEntity?.code === item?.productEntity?.code &&
              el?.contractType?.code === item?.contract?.code
          )
        );
      },
      icon: <EditIcon />,
      color: AppColors.secondary,
    },
    {
      title: "Xóa",
      callback: (item) => {
        setIsRemove();
        setItemSelected(
          selloutPrices?.find(
            (el) =>
              el?.productEntity?.code === item?.productEntity?.code &&
              el?.contractType?.code === item?.contract?.code
          )
        );
      },
      icon: <DeleteIcon />,
      color: AppColors.error,
    },
  ];

  const mergedProductContractData = useMemo(() => {
    let mergedProducts = sellinPrices
      ?.map((pro) => {
        return contract?.content?.map((ctr) => {
          let newPro = { ...pro };
          newPro["contract"] = ctr;
          return newPro;
        });
      })
      .flat();
    return mergedProducts?.map((pro, index) => {
      return {
        ...pro,
        id: index,
      };
    });
  }, [sellinPrices, contract]);

  const renderPriceCell = useCallback(
    (params) => {
      const selloutIndex = selloutPrices?.findIndex(
        (el) =>
          el?.productEntity?.code === params?.row?.productEntity?.code &&
          el?.contractType?.code === params?.row?.contract?.code
      );
      let contractDiscount = selloutPrices?.[selloutIndex]?.contractDiscount;
      let massDiscount = selloutPrices?.[selloutIndex]?.massDiscount;
      let distributionDiscount =
        selloutPrices?.[selloutIndex]?.contractType?.channel?.promotion;
      const mergedProductIndex = mergedProductContractData?.findIndex(
        (el) =>
          el?.productEntity?.code === params?.row?.productEntity?.code &&
          el?.contract?.code === params?.row?.contract?.code
      );
      let priceBeforeVat =
        mergedProductContractData[mergedProductIndex]?.priceBeforeVat;
      let vat = mergedProductContractData[mergedProductIndex]?.vat;
      let priceAfterAll =
        (((priceBeforeVat * (100 + vat)) / 100) *
          (100 - contractDiscount - massDiscount - distributionDiscount)) /
        100;
      return priceAfterAll ? (
        <Typography>
          <Typography>{formatVietnameseCurrency(priceAfterAll)}</Typography>
        </Typography>
      ) : (
        "Chưa xác định"
      );
    },
    [selloutPrices, mergedProductContractData]
  );
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Box>
        <CustomToolBar
          actions={actions}
          containSearch={false}
          containFilter={false}
        />
      </Box>
      <FormProvider {...methods}>
        <CustomDataGrid
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
          isLoading={isLoadingProduct || isLoadingContract}
          // totalItem={mergedProductContractData.length}
          isSelectable
          isEditable={(params) => {
            return !selloutPrices?.find(
              (el) =>
                el?.productEntity?.code === params?.row?.productEntity?.code &&
                el?.contractType?.code === params?.row?.contract?.code
            );
          }}
          columns={[
            saleOrderPrices[0],
            saleOrderPrices[1],
            saleOrderPrices[2],
            saleOrderPrices[3],
            saleOrderPrices[4],
            {
              field: "massDiscount",
              headerName: "Chiết khấu bán sỉ (%)",
              sortable: false,
              minWidth: 150,
              type: "number",
              editable: true,
              headerAlign: "center",
              align: "center",
              renderCell: (params) => {
                const price = productSalePrices?.find(
                  (el) => el.id === params.id
                );
                const selloutPrice = selloutPrices?.find(
                  (el) =>
                    el?.productEntity?.code ===
                      params?.row?.productEntity?.code &&
                    el?.contractType?.code === params?.row?.contract?.code
                );
                return (
                  <Typography sx={{ fontSize: 14 }}>
                    {selloutPrice
                      ? `${selloutPrice?.massDiscount} %`
                      : price
                      ? price?.massDiscount
                      : "Nhập %"}
                  </Typography>
                );
              },
              renderEditCell: (params) => {
                const index = productSalePrices?.findIndex(
                  (el) => el.id === params.id
                );
                const value =
                  index !== -1 ? productSalePrices[index]?.massDiscount : null;
                return (
                  <Controller
                    name={`productSalePrices.${index}.massDiscount`}
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
                        placeholder="Nhập giá tiền"
                        value={value}
                        onChange={onChange}
                      />
                    )}
                  />
                );
              },
            },
            {
              field: "contractDiscount",
              headerName: "Chiết khấu hợp đồng (%)",
              sortable: false,
              minWidth: 150,
              type: "number",
              editable: true,
              headerAlign: "center",
              align: "center",
              renderCell: (params) => {
                const price = productSalePrices?.find(
                  (el) => el.id === params.id
                );
                const selloutPrice = selloutPrices?.find(
                  (el) =>
                    el?.productEntity?.code ===
                      params?.row?.productEntity?.code &&
                    el?.contractType?.code === params?.row?.contract?.code
                );
                return (
                  <Typography sx={{ fontSize: 14 }}>
                    {selloutPrice
                      ? `${selloutPrice?.contractDiscount} %`
                      : price
                      ? price?.contractDiscount
                      : "Nhập %"}
                  </Typography>
                );
              },
              renderEditCell: (params) => {
                const index = productSalePrices?.findIndex(
                  (el) => el.id === params.id
                );
                const value =
                  index !== -1
                    ? productSalePrices[index]?.contractDiscount
                    : null;
                return (
                  <Controller
                    name={`productSalePrices.${index}.contractDiscount`}
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
                        placeholder="Nhập giá tiền"
                        value={value}
                        onChange={onChange}
                      />
                    )}
                  />
                );
              },
            },
            {
              field: "distributionDiscount",
              headerName: "Chiết khấu kênh phân phối (%)",
              sortable: false,
              minWidth: 150,
              type: "number",
              editable: true,
              headerAlign: "center",
              align: "center",
              renderCell: (params) => {
                return (
                  <Typography sx={{ fontSize: 14 }}>
                    {`${params?.row?.contract?.channel?.promotion} %`}
                  </Typography>
                );
              },
            },
            {
              field: "priceAfterAll",
              headerName: "Giá cuối cùng",
              sortable: false,
              minWidth: 150,
              headerAlign: "center",
              align: "center",
              renderCell: (params) => {
                return renderPriceCell(params);
              },
            },
            {
              field: "action",
              headerName: "Hành động",
              headerAlign: "center",
              align: "center",
              sortable: false,
              flex: 1,
              minWidth: 150,
              type: "actions",
              getActions: (params) => {
                let price = selloutPrices?.find(
                  (el) =>
                    el?.productEntity?.code ===
                      params?.row?.productEntity?.code &&
                    el?.contractType?.code === params?.row?.contract?.code
                );
                return [
                  ...extraActions.map((extraAction, index) => (
                    <Action
                      item={params.row}
                      key={index}
                      extraAction={extraAction}
                      onActionCall={extraAction.callback}
                      disabled={!price}
                    />
                  )),
                ];
              },
            },
          ]}
          rows={mergedProductContractData ? mergedProductContractData : []}
          onSelectionChange={(ids) => {
            let results = mergedProductContractData.filter((pro) =>
              ids.includes(pro?.id)
            );
            setValue("productSalePrices", results);
          }}
        />
      </FormProvider>
      <CustomDrawer open={isOpenDrawer} onClose={setOpenDrawer}>
        <HeaderModal
          onClose={setOpenDrawer}
          title="Sửa thông tin giá sản phẩm"
        />
        <UpdateSalePrice currPrice={itemSelected} setIsAdd={setOpenDrawer} />
      </CustomDrawer>
      <DraggableDeleteDialog
        // disable={isLoadingRemove}
        open={isRemove && itemSelected}
        handleOpen={setIsRemove}
        callback={async (flag) => {
          if (flag) {
            await deleteSalePrices.mutateAsync();
          }
          setIsRemove(false);
        }}
      />
    </Box>
  );
}

const SCR_ID = "SCR_SCM_SALE_PRICE";
export default withScreenSecurity(SalePriceScreen, SCR_ID, true);
