import { Routes, Route } from "react-router-dom";
import ProductList from "../views/admin/product/ProductList";
import AddProduct from "../views/admin/product/AddProduct";
import ReceiptBill from "../views/admin/receipt/ReceiptBill";
import ReceiptList from "../views/admin/receipt/ReceiptList";
import ReceiptItem from "../views/admin/receipt/ReceiptItem";
import ReceiptDetail from "../views/admin/receipt/ReceiptDetail"
import InventoryList from "../views/admin/inventory/InventoryList";
import OrderList from "../views/admin/order/OrderList";
import OrderItem from "../views/admin/order/OrderItem";
import OrderDetail from "../views/admin/order/OrderDetail";
function AdminRouter() {
  return (
    <Routes>
      <Route path="inventory" element={<InventoryList />} />
      <Route path="product" element={<ProductList />} />
      <Route path="product/add-product" element={<AddProduct />} />
      <Route path="product/:id" element={<AddProduct />} />
      <Route path="orders" element={<OrderList />} />
      <Route path="orders/:id1" element={<OrderDetail />} />
      <Route path="orders/:id1/:id2" element={<OrderItem />} />
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/:id1" element={<ReceiptDetail />} />
      <Route path="receipts/:id1/:id2" element={<ReceiptItem />} />
      <Route path="receipts/receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
}

export default AdminRouter;

