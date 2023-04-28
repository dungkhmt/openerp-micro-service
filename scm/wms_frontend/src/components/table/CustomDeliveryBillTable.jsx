import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";

const CustomDeliveryBillTable = ({ orderItem, billItem, product_facility }) => {
  console.log(orderItem, billItem, product_facility);
  const rows = orderItem?.map((item) => {
    let mappedBill = billItem.filter(
      (bill) => bill?.product?.code === item?.product?.code
    );
    let facilityInventory = product_facility.filter(
      (inv) => inv?.product?.code === item?.product?.code
    );
    // console.log(
    //   mappedBill.reduce((accumulator, curr) => {
    //     return accumulator + curr.effectiveQty;
    //   }, 0)
    // );
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
      inventory: facilityInventory[0]?.inventoryQty
        ? facilityInventory[0]?.inventoryQty
        : 0,
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
            <TableCell align="right">Số lượng đã xuất kho</TableCell>
            <TableCell align="right">Tổng số lượng cần xuất</TableCell>
            <TableCell align="right">Tồn kho hiện tại</TableCell>
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
              <TableCell align="right">{row.inventory}</TableCell>
              {/* <TableCell align="right">{row.total_money}</TableCell> */}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default CustomDeliveryBillTable;
