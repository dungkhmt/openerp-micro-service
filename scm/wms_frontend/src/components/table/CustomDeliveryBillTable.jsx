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
const CustomDeliveryBillTable = ({ orderItem, billItem, product_facility }) => {
  console.log(orderItem, billItem, product_facility);
  const rows = orderItem?.map((item) => {
    let mappedBill = billItem.filter(
      (bill) => bill?.product?.code === item?.product?.code
    );
    let facilityInventory = product_facility.filter(
      (inv) => inv?.product?.code === item?.product?.code
    );
    return {
      name: item?.product?.name,
      effQty:
        mappedBill.length > 0
          ? mappedBill?.reduce(
              (accumulator, curr) => accumulator + curr.effectiveQty,
              0
            )
          : 0,
      qty: item?.quantity,
      inventory: facilityInventory[0]?.inventoryQty
        ? facilityInventory[0]?.inventoryQty
        : 0,
    };
  });
  return (
    <TableContainer component={Paper}>
      <Table sx={{ minWidth: 650 }} aria-label="simple table">
        <TableHead>
          <StyledTableRow>
            <StyledTableCell align="right">Tên sản phẩm</StyledTableCell>
            <StyledTableCell align="right">
              Số lượng đã xuất kho
            </StyledTableCell>
            <StyledTableCell align="right">
              Tổng số lượng cần xuất
            </StyledTableCell>
            <StyledTableCell align="right">Tồn kho hiện tại</StyledTableCell>
          </StyledTableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow
              key={row.name}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <StyledTableCell align="right">{row.name}</StyledTableCell>
              <StyledTableCell align="right">{row.effQty}</StyledTableCell>
              <StyledTableCell align="right">{row.qty}</StyledTableCell>
              <StyledTableCell align="right">{row.inventory}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomDeliveryBillTable;
