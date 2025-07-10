import { Routes, Route } from "react-router-dom";
import RevenueReport from "../views/director/RevenueReport";
import ProductCategoryReport from "../views/director/ProductCategoryReport";
const DirectorRouter = () => {
  return (
    <Routes>
      <Route path="revenue" element={<RevenueReport />} />
      <Route path="category" element={<ProductCategoryReport />} />
    </Routes>
  );
};

export default DirectorRouter;