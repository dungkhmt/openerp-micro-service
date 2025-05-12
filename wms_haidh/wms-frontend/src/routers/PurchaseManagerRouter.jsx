import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_manager/ReceiptList";
import ReceiptDetail from "../views/purchase_manager/ReceiptDetail";
const PurchaseManagerRouter = () => {
  return (
    <Routes>
      <Route path="process-receipts" element={<ReceiptList />} />
      <Route path="process-receipts/:receiptId" element={<ReceiptDetail/>} />
    </Routes>
  );
  };
  
  export default PurchaseManagerRouter;