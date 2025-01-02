import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/sale_manager/ReceiptList";
import ReceiptDetail from "../views/sale_manager/ReceiptDetail";
import SaleOrderList from "../views/sale_manager/SaleOrderList";
import SaleOrderDetail from "../views/sale_manager/SaleOrderDetail";

const SaleManagerRouter = () => {
  return (
    <Routes>
      <Route path="price-config" element={<div>Price config</div>} />
      <Route path="receipt" element={<ReceiptList/>} />
      <Route path="receipt/:receiptId" element={<ReceiptDetail/>} />
      <Route path="sale-order" element={<SaleOrderList/>} />
      <Route path="sale-order/:orderId" element={<SaleOrderDetail/>} />
    </Routes>
  );
  };
  
  export default SaleManagerRouter;