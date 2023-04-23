import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, Typography } from "@mui/material";
import { green } from "@mui/material/colors";
import { useState } from "react";
import { useToggle, useWindowSize } from "react-use";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import CustomDataGrid from "../../components/datagrid/CustomDataGrid";
import CustomDrawer from "../../components/drawer/CustomDrawer";
import {
  useGetFacilityInventory,
  useGetFacilityList,
} from "../../controllers/query/facility-query";
import { Action } from "../sellin/PurchaseOrder";
import { staticDatagridCols, staticProductFields } from "./LocalConstant";
function FacilityScreen({ screenAuthorization }) {
  const [params, setParams] = useState({
    page: 1,
    page_size: 50,
  });
  const { height } = useWindowSize();

  const [isOpenDrawer, setOpenDrawer] = useToggle(false);
  const [facilityCode, setFacilityCode] = useState("");

  const { isLoading, data } = useGetFacilityList();
  const { isLoading: isLoadingInventory, data: inventory } =
    useGetFacilityInventory({
      code: facilityCode,
    });
  const extraActions = [
    {
      title: "Sửa",
      callback: (item) => {
        // setItemSelected(item);
        // setIsEdit();
      },
      icon: <EditIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_EDIT,
    },
    {
      title: "Xóa",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
      },
      icon: <DeleteIcon />,
      // permission: PERMISSIONS.MANAGE_CATEGORY_DELETE,
    },
    {
      title: "Xem",
      callback: (item) => {
        console.log("Item: ", item);
        setOpenDrawer((pre) => !pre);
        setFacilityCode(item?.code);
      },
      icon: <VisibilityIcon />,
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
          color={green[800]}
          fontSize={17}
        >
          {"KHO HÀNG"}
        </Typography>
      </Box>
      <CustomDataGrid
        params={params}
        setParams={setParams}
        sx={{ height: height - 64 - 71 - 24 - 20 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom
        isLoading={isLoading}
        totalItem={100}
        columns={[
          ...staticDatagridCols,
          {
            field: "quantity",
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
        rows={data ? data?.content : []}
      />
      <CustomDrawer
        open={isOpenDrawer}
        onClose={setOpenDrawer}
        // style={{ zIndex: 1000 }}
      >
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
            color={green[800]}
            fontSize={17}
          >
            {"TỒN KHO"}
          </Typography>
        </Box>
        <CustomDataGrid
          isSelectable={false}
          params={params}
          setParams={setParams}
          sx={{ height: height - 64 - 71 - 24 - 20 - 35 }} // Toolbar - Searchbar - TopPaddingToolBar - Padding bottom - Page Title
          isLoading={isLoadingInventory}
          totalItem={100}
          columns={staticProductFields}
          rows={inventory ? inventory?.content : []}
        />
      </CustomDrawer>
    </Box>
  );
}

const SCR_ID = "SCR_WAREHOUSE";
export default withScreenSecurity(FacilityScreen, SCR_ID, true);
