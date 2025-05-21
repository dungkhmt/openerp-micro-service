import { Routes, Route } from "react-router-dom";
import ProductList from "../views/warehouse_manager/product/ProductList";
import AddProduct from "../views/warehouse_manager/product/AddProduct";
import ReceiptList from "../views/warehouse_manager/purchase_order/ReceiptList";
import ReceiptItem from "../views/warehouse_manager/purchase_order/ReceiptItem";
import ReceiptDetail from "../views/warehouse_manager/purchase_order/ReceiptDetail"
import InventoryList from "../views/warehouse_manager/warehouse/InventoryList";
import OrderList from "../views/warehouse_manager/sale_order/OrderList";
import OrderItem from "../views/warehouse_manager/sale_order/OrderItem";
import OrderDetail from "../views/warehouse_manager/sale_order/OrderDetail";
import WarehouseList from "../views/warehouse_manager/warehouse/WarehouseList";
import WarehouseLayout from "../views/warehouse_manager/warehouse/WarehouseLayout";

function WarehouseManagerRouter() {
  return (
    <Routes>
      <Route path="warehouse" element={<WarehouseList />} />
      <Route path="warehouse/:id" element={<WarehouseLayout />} />
      <Route path="warehouse/:id1/:id2" element={<InventoryList />} />
      <Route path="product" element={<ProductList />} />
      <Route path="product/add-product" element={<AddProduct />} />
      <Route path="product/:id" element={<AddProduct />} />
      <Route path="orders" element={<OrderList />} />
      <Route path="orders/:id1" element={<OrderDetail />} />
      <Route path="orders/:id1/:id2" element={<OrderItem />} />
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/:id1" element={<ReceiptDetail />} />
      <Route path="receipts/:id1/:id2" element={<ReceiptItem />} />
    </Routes>
  );
}

export default WarehouseManagerRouter;

