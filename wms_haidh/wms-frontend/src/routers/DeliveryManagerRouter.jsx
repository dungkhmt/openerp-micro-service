import { Routes, Route } from "react-router-dom";
import DeliveryTripPath from "../views/delivery_manager/DeliveryTripPath";
import DeliveryTrip from "../views/delivery_manager/DeliveryTrip";
import AddTrip from "../views/delivery_manager/AddTrip";
function DeliveryManagerRouter() {
  return (
    <Routes>
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/:deliveryTripId" element={<DeliveryTripPath />} />
      <Route path="delivery-trip/add-trip" element={<AddTrip />} /> 
    </Routes>
  );
}

export default DeliveryManagerRouter;