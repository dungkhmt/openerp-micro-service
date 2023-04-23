import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";

function createData(code, name, quantity, price_unit, total_money) {
  return { code, name, quantity, price_unit, total_money };
}
const CustomBillTable = ({ orderItem, billItem }) => {
  const rows = orderItem?.map((item) => {
    let mappedBill = billItem.filter(
      (bill) => bill?.product?.code === item?.product?.code
    );
    return {
      name: item?.product?.name,
      effQty: mappedBill ? mappedBill[0]?.effectiveQty : 0,
      qty: item?.quantity,
      // item?.purchaseOrder?.totalMoney
    };
  });
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            {/* <TableCell>Mã bill</TableCell> */}
            <TableCell align="right">Tên sản phẩm</TableCell>
            <TableCell align="right">Số lượng đã nhập kho</TableCell>
            <TableCell align="right">Tổng số lượng cần nhập</TableCell>
            {/* <TableCell align="right">Tổng cộng</TableCell> */}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow
              key={row.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              {/* <TableCell component="th" scope="row">
                {row.code}
              </TableCell> */}
              <TableCell align="right">{row.name}</TableCell>
              <TableCell align="right">{row.effQty}</TableCell>
              <TableCell align="right">{row.qty}</TableCell>
              {/* <TableCell align="right">{row.total_money}</TableCell> */}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomBillTable;
