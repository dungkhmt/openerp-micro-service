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
import { formatVietnameseCurrency } from "../../utils/GlobalUtils";

function createData(code, name, quantity, price_unit) {
  return { code, name, quantity, price_unit };
}
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

const CustomOrderTable = ({ items, currOrder }) => {
  const rows = items?.map((item) =>
    createData(
      item?.product?.code,
      item?.product?.name,
      item?.quantity,
      item?.priceUnit
    )
  );
  return (
    <TableContainer
      component={Paper}
      sx={{ maxWidth: "50vw", alignSelf: "center" }}
    >
      <Table sx={{ minWidth: 700 }} aria-label="simple table">
        <TableHead>
          <StyledTableRow>
            <StyledTableCell align="center" colSpan={2}>
              Sản phẩm
            </StyledTableCell>
            <StyledTableCell align="right" colSpan={3}>
              Thành tiền
            </StyledTableCell>
          </StyledTableRow>
          <StyledTableRow>
            <StyledTableCell>Mã sản phẩm</StyledTableCell>
            <StyledTableCell>Tên sản phẩm</StyledTableCell>
            <StyledTableCell align="right">Số lượng</StyledTableCell>
            <StyledTableCell align="right">Đơn giá</StyledTableCell>
            <StyledTableCell align="right">Tổng cộng</StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow
              key={row.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <StyledTableCell component="th" scope="row">
                {row.code}
              </StyledTableCell>
              <StyledTableCell>{row.name}</StyledTableCell>
              <StyledTableCell align="right">{row.quantity}</StyledTableCell>
              <StyledTableCell align="right">
                {formatVietnameseCurrency(row.price_unit)}
              </StyledTableCell>
              <StyledTableCell align="right">
                {formatVietnameseCurrency(row.quantity * row.price_unit)}
              </StyledTableCell>
            </StyledTableRow>
          ))}
          <StyledTableRow>
            <StyledTableCell rowSpan={3} colSpan={2} />
            <StyledTableCell colSpan={2}>Tổng cộng</StyledTableCell>
            <StyledTableCell align="right">
              {formatVietnameseCurrency(currOrder?.totalMoney)}
            </StyledTableCell>
          </StyledTableRow>
          <StyledTableRow>
            <StyledTableCell colSpan={1}>
              {currOrder?.vat !== undefined ? "Thuế" : "Khuyến mãi"}
            </StyledTableCell>
            <StyledTableCell align="right">{`${
              currOrder?.vat !== undefined
                ? currOrder?.vat
                : currOrder?.discount
            } %`}</StyledTableCell>
            <StyledTableCell align="right">
              {formatVietnameseCurrency(
                currOrder?.totalMoney *
                  (currOrder?.vat !== undefined
                    ? currOrder?.vat / 100
                    : currOrder?.discount / 100)
              )}
            </StyledTableCell>
          </StyledTableRow>
          <StyledTableRow>
            <StyledTableCell colSpan={2}>Tổng phải trả</StyledTableCell>
            <StyledTableCell align="right">
              {formatVietnameseCurrency(currOrder?.totalPayment)}
            </StyledTableCell>
          </StyledTableRow>
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomOrderTable;
