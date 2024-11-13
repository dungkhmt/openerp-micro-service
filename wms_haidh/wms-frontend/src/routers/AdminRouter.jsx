import { Routes, Route } from "react-router-dom";
import BaseTable from "../views/BaseTable";
import AddProduct from "../views/AddProduct"; // Nhập component thêm sản phẩm

function AdminRouter() {
  return (
    <Routes>
      <Route path="warehouse" element={<div>Warehouse list</div>} />
      <Route path="product" element={<BaseTable />} />
      <Route path="product/add-product" element={<AddProduct />} /> 
      <Route path="product/:id" element={<AddProduct />} /> 
      <Route path="orders" element={<div>Order list</div>} />
      <Route path="process-receipts" element={<div>Receipts list</div>} />
    </Routes>
  );
}

export default AdminRouter;

