import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_manager/ReceiptList";
import ReceiptDetail from "../views/purchase_manager/ReceiptDetail";
import ReceiptBill from "../views/purchase_manager/ReceiptBill";
const PurchaseManagerRouter = () => {
  return (
    <Routes>
      <Route path="process-receipts" element={<ReceiptList />} />
      <Route path="process-receipts/:receiptId" element={<ReceiptDetail/>} />
      <Route path="receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
  };
  
  export default PurchaseManagerRouter;