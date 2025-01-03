import { Routes, Route } from "react-router-dom";
import SaleOrderList from "../views/sale_manager/SaleOrderList";
import SaleOrderDetail from "../views/sale_manager/SaleOrderDetail";

const SaleManagerRouter = () => {
  return (
    <Routes>
      <Route path="price-config" element={<div>Price config</div>} />
      <Route path="sale-order" element={<SaleOrderList/>} />
      <Route path="sale-order/:orderId" element={<SaleOrderDetail/>} />
    </Routes>
  );
  };
  
  export default SaleManagerRouter;