import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  styled,
  tableCellClasses,
} from "@mui/material";
const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: theme.palette.action.hover,
  },
  // hide last border
  "&:last-child td, &:last-child th": {
    border: 0,
  },
}));
const CustomBillTable = ({ orderItem, billItem }) => {
  const rows = orderItem?.map((item) => {
    let mappedBill = billItem.filter(
      (bill) => bill?.product?.code === item?.product?.code
    );
    return {
      name: item?.product?.name,
      effQty:
        mappedBill.length > 0
          ? mappedBill.reduce(
              (accumulator, curr) => accumulator + curr.effectiveQty,
              0
            )
          : 0,
      qty: item?.quantity,
    };
  });
  return (
    <TableContainer
      component={Paper}
      sx={{ maxWidth: "50vw", alignSelf: "center" }}
    >
      <Table sx={{ minWidth: 700 }} aria-label="simple table">
        <TableHead>
          <StyledTableRow>
            <StyledTableCell align="center">Tên sản phẩm</StyledTableCell>
            <StyledTableCell align="right">
              Số lượng đã nhập kho
            </StyledTableCell>
            <StyledTableCell align="right">
              Tổng số lượng cần nhập
            </StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {rows?.map((row) => (
            <StyledTableRow
              key={row.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <StyledTableCell align="center">{row.name}</StyledTableCell>
              <StyledTableCell align="right">{row.effQty}</StyledTableCell>
              <StyledTableCell align="right">{row.qty}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomBillTable;
