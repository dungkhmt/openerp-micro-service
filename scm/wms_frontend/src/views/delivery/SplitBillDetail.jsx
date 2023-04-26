import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import {
  Box,
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
import { useLocation } from "react-router-dom";
import withScreenSecurity from "../../components/common/withScreenSecurity";
import { useGetBillItemsOfBill } from "../../controllers/query/bill-query";
import { AppColors } from "../../shared/AppColors";
function SplitBillDetailScreen({ screenAuthorization }) {
  const location = useLocation();
  const currBills = location.state.bills;
  const { isLoading: isLoadingBillItem, data: deliveryBillItems } =
    useGetBillItemsOfBill({
      bill_code: currBills?.code,
    });

  let actions = [
    {
      title: "Chia đơn",
      callback: (pre) => {},
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
      title: "Xóa",
      callback: (item) => {
        // setIsRemove();
        // setItemSelected(item);
      },
      icon: <DeleteIcon />,
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
                <TableCell align="right">{row.orderSeqId}</TableCell>
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
    </Box>
  );
}

const SCR_ID = "SCR_SPLIT_BILL_DETAIL";
export default withScreenSecurity(SplitBillDetailScreen, SCR_ID, true);
