import { Routes, Route } from "react-router-dom";
import DeliveryTripPath from "../views/delivery_manager/DeliveryTripPath";
import DeliveryTrip from "../views/delivery_manager/DeliveryTrip";
function DeliveryManagerRouter() {
  return (
    <Routes>
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/:deliveryTripId" element={<DeliveryTripPath />} />
    </Routes>
  );
}

export default DeliveryManagerRouter;