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
const CustomOrderTable = ({ items }) => {
  const rows = items?.map((item) =>
    createData(
      item?.product?.code,
      item?.product?.name,
      item?.quantity,
      item?.priceUnit,
      item?.purchaseOrder?.totalMoney
    )
  );
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Mã sản phẩm</TableCell>
            <TableCell align="right">Tên sản phẩm</TableCell>
            <TableCell align="right">Số lượng</TableCell>
            <TableCell align="right">Đơn giá</TableCell>
            <TableCell align="right">Tổng cộng</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow
              key={row.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell component="th" scope="row">
                {row.code}
              </TableCell>
              <TableCell align="right">{row.name}</TableCell>
              <TableCell align="right">{row.quantity}</TableCell>
              <TableCell align="right">{row.price_unit}</TableCell>
              <TableCell align="right">{row.total_money}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomOrderTable;
