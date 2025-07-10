import { Routes, Route } from "react-router-dom";
import DeliveryTrip from "../views/delivery_staff/DeliveryTripList";
import DeliveryTripDetail from "../views/delivery_staff/DeliveryTripDetail";
import DeliveryTripItem from "../views/delivery_staff/DeliveryTripItem";
const PurchasePersonRouter = () => {
  return (
    <Routes>
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/:id" element={<DeliveryTripDetail />} />
      <Route path="delivery-trip/:id1/:id2" element={<DeliveryTripItem />} />
    </Routes>
  );
};

export default PurchasePersonRouter;