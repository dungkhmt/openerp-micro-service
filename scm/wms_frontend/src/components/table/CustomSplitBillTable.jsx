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

const CustomSplitBillTable = ({ items, currOrder }) => {
  return (
    <TableContainer
      component={Paper}
      sx={{ maxWidth: "50vw", alignSelf: "center" }}
    >
      <Table sx={{ minWidth: 700 }} aria-label="simple table">
        <TableHead>
          <StyledTableRow>
            <StyledTableCell align="right">Tên sản phẩm</StyledTableCell>
            <StyledTableCell align="right">Lượng thực xuất</StyledTableCell>
            <StyledTableCell align="right">Seq id</StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {items?.map((row) => (
            <StyledTableRow
              key={row?.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <StyledTableCell align="right">
                {row?.product?.name}
              </StyledTableCell>
              <StyledTableCell align="right">
                {row?.effectiveQty}
              </StyledTableCell>
              <StyledTableCell align="right">{row.seqId}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomSplitBillTable;
