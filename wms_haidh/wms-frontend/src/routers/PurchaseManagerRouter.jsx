import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_manager/ReceiptList";
import AddReceipt from "../views/purchase_manager/AddReceipt";
import ReceiptBill from "../views/purchase_manager/ReceiptBill";
import ReceiptDetail from "../views/purchase_manager/ReceiptDetail";
const PurchaseManagerRouter = () => {
  return (
    <Routes>
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/add-receipt" element={<AddReceipt />} /> 
      <Route path="receipts/:receiptId" element={<ReceiptDetail />} /> 
      <Route path="receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
  };
  
  export default PurchaseManagerRouter;