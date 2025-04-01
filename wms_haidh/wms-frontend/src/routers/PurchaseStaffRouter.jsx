import { Routes, Route } from "react-router-dom";
import ReceiptList from "../views/purchase_staff/ReceiptList";
import ReceiptDetail from "../views/purchase_staff/ReceiptDetail";
import AddReceipt from "../views/purchase_staff/AddReceipt";
const PurchaseStaffRouter = () => {
  return (
    <Routes>
      <Route path="receipts" element={<ReceiptList />} />
      <Route path="receipts/add-receipt" element={<AddReceipt />} /> 
      <Route path="receipts/:receiptId" element={<ReceiptDetail />} /> 
    </Routes>
  );
  };
  
  export default PurchaseStaffRouter;