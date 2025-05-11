import { Routes, Route } from "react-router-dom";
import ProductList from "../views/warehouse_manager/product/ProductList";
import AddProduct from "../views/warehouse_manager/product/AddProduct";
import ReceiptBill from "../views/warehouse_manager/receipt/ReceiptBill";
import ReceiptList from "../views/warehouse_manager/receipt/ReceiptList";
import ReceiptItem from "../views/warehouse_manager/receipt/ReceiptItem";
import ReceiptDetail from "../views/warehouse_manager/receipt/ReceiptDetail"
import InventoryList from "../views/warehouse_manager/inventory/InventoryList";
import OrderList from "../views/warehouse_manager/order/OrderList";
import OrderItem from "../views/warehouse_manager/order/OrderItem";
import OrderDetail from "../views/warehouse_manager/order/OrderDetail";
import WarehouseList from "../views/warehouse_manager/warehouse/WarehouseList";
import WarehouseLayout from "../views/warehouse_manager/warehouse/WarehouseLayout";

function WarehouseManagerRouter() {
  return (
    <Routes>
      <Route path="warehouse" element={<WarehouseList />} />
      <Route path="warehouse/:id" element={<WarehouseLayout />} />
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

export default WarehouseManagerRouter;

