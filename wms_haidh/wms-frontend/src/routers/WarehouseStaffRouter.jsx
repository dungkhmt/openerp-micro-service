import { Routes, Route } from "react-router-dom";
import WarehouseList from "../views/warehouse_staff/WarehouseList";
import OrderPicking from "../views/warehouse_staff/OrderPicking";
import Putaway from "../views/warehouse_staff/Putaway";

function WarehouseStaffRouter() {
  return (
    <Routes>
      <Route path="warehouse" element={<WarehouseList />} />
      <Route path="warehouse/order-picking/:id" element={<OrderPicking/>} />
      <Route path="warehouse/putaway/:id" element={<Putaway/>} />
    </Routes>
  );
}

export default WarehouseStaffRouter;

