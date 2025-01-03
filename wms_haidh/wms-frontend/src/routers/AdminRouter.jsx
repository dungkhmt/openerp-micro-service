import { Routes, Route } from "react-router-dom";
import ProductList from "../views/admin/ProductList";
import AddProduct from "../views/admin/AddProduct";
import ReceiptBill from "../views/admin/ReceiptBill";
import ReceiptList from "../views/admin/ReceiptList";
import ReceiptItemRequest from "../views/admin/ReceiptItemRequest";
import InventoryList from "../views/admin/InventoryList";
import OrderList from "../views/admin/OrderList";
import OrderItem from "../views/admin/OrderItem";
function AdminRouter() {
  return (
    <Routes>
      <Route path="inventory" element={<InventoryList />} />
      <Route path="product" element={<ProductList />} />
      <Route path="product/add-product" element={<AddProduct />} />
      <Route path="product/:id" element={<AddProduct />} />
      <Route path="orders" element={<OrderList />} />
      <Route path="orders/:id" element={<OrderItem />} />
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/:id" element={<ReceiptItemRequest />} />
      <Route path="receipts/receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
}

export default AdminRouter;

