import { Routes, Route } from "react-router-dom";
import SaleOrderList from "../views/sale_manager/SaleOrderList";
import SaleOrderDetail from "../views/sale_manager/SaleOrderDetail";
import PriceConfig from "../views/sale_manager/PriceConfig";
import ProductPriceList from "../views/sale_manager/ProductPriceList";
const SaleManagerRouter = () => {
  return (
    <Routes>
      <Route path="price-config" element={<ProductPriceList />} />
      <Route path="price-config/:id" element={<PriceConfig />} />
      <Route path="sale-order" element={<SaleOrderList />} />
      <Route path="sale-order/:orderId" element={<SaleOrderDetail />} />
    </Routes>
  );
};

export default SaleManagerRouter;