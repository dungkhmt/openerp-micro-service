import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { unix } from "moment";

const BillDetailModal = ({
  setSeeBillDetail,
  billItemsOfBill,
  isLoadingBillItem,
}) => {
  // Finding the Total Cost
  console.log("Bill ", billItemsOfBill);
  return (
    <TableContainer
      component={Paper}
      sx={{ borderRadius: 5, borderWidth: 5, padding: 2 }}
    >
      <Table aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Tên sản phẩm</TableCell>
            <TableCell align="right">SeqId</TableCell>
            <TableCell align="right">Số lượng</TableCell>
            <TableCell align="right">Ngày nhận</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {billItemsOfBill?.map((row) => (
            <TableRow key={row.number}>
              <TableCell component="th" scope="row">
                {row?.product?.name}
              </TableCell>
              <TableCell align="right">{row?.seqId}</TableCell>
              <TableCell align="right">{row?.effectiveQty}</TableCell>
              <TableCell align="right">
                {unix(
                  row?.receivingDate ? row?.receivingDate : row?.updatedDate
                )?.format("DD-MM-YYYY")}
              </TableCell>
            </TableRow>
          ))}
          <TableRow>
            <TableCell align="right" colSpan={4}>
              {/* <b>Total Cost:</b> ${totalCost} */}
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default BillDetailModal;
