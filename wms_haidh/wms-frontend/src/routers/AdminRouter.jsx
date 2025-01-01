import { Routes, Route } from "react-router-dom";
import BaseTable from "../views/admin/ProductList";
import AddProduct from "../views/admin/AddProduct"; 
import ReceiptBill from "../views/admin/ReceiptBill";
import ReceiptList from "../views/admin/ReceiptList";
function AdminRouter() {
  return (
    <Routes>
      <Route path="warehouse" element={<div>Warehouse list</div>} />
      <Route path="product" element={<BaseTable />} />
      <Route path="product/add-product" element={<AddProduct />} /> 
      <Route path="product/:id" element={<AddProduct />} /> 
      <Route path="orders" element={<div>Order list</div>} />
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/:id" element={<BaseTable />} />
      <Route path="receipts/receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
}

export default AdminRouter;

