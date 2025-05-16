import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_manager/ReceiptList";
import ReceiptDetail from "../views/purchase_manager/ReceiptDetail";
import SupplierList from "../views/purchase_manager/SupplierList";
const PurchaseManagerRouter = () => {
  return (
    <Routes>
      <Route path="suppliers" element={<SupplierList />} />
      <Route path="process-receipts" element={<ReceiptList />} />
      <Route path="process-receipts/:receiptId" element={<ReceiptDetail/>} />
    </Routes>
  );
  };
  
  export default PurchaseManagerRouter;