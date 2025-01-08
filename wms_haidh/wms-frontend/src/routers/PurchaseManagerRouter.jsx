import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_manager/ReceiptList";
import AddReceipt from "../views/purchase_manager/AddReceipt";
import ReceiptBill from "../views/purchase_manager/ReceiptBill";
import ReceiptDetail from "../views/purchase_manager/ReceiptDetail";
import ReceiptApproveList from "../views/purchase_manager/ReceiptApproveList";
import ReceiptApproveDetail from "../views/purchase_manager/ReceiptApproveDetail";
const PurchaseManagerRouter = () => {
  return (
    <Routes>
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/add-receipt" element={<AddReceipt />} /> 
      <Route path="receipts/:receiptId" element={<ReceiptDetail />} /> 
      <Route path="process-receipts" element={<ReceiptApproveList />} />
      <Route path="process-receipts/:receiptId" element={<ReceiptApproveDetail/>} />
      <Route path="receipt-bill" element={<ReceiptBill />} />
    </Routes>
  );
  };
  
  export default PurchaseManagerRouter;