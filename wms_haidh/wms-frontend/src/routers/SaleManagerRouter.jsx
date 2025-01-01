import { Routes, Route } from "react-router-dom";
import BaseTable from "../views/admin/ProductList";
import ReceiptList from "../views/sale_manager/ReceiptList";
import ReceiptDetail from "../views/sale_manager/ReceiptDetail";

const SaleManagerRouter = () => {
  return (
    <Routes>
      <Route path="price-config" element={<div>Price config</div>} />
      <Route path="receipt" element={<ReceiptList/>} />
      <Route path="receipt/:receiptId" element={<ReceiptDetail/>} />
      <Route path="sale-order" element={<BaseTable/>} />
    </Routes>
  );
  };
  
  export default SaleManagerRouter;