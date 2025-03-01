import { Routes, Route } from "react-router-dom";
import DeliveryTripPath from "../views/delivery_manager/DeliveryTripPath";
import DeliveryTrip from "../views/delivery_manager/DeliveryTrip";
import AddTrip from "../views/delivery_manager/AddTrip";
import DeliveryPerson from "../views/delivery_manager/DeliveryPerson";
import Shipment from "../views/delivery_manager/Shipment";
function DeliveryManagerRouter() {
  return (
    <Routes>
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/:deliveryTripId" element={<DeliveryTripPath />} />
      <Route path="delivery-trip/add-trip" element={<AddTrip />} /> 
      <Route path="delivery-person" element={<DeliveryPerson />} />
      <Route path="shipments" element={<Shipment />} />
    </Routes>
  );
}

export default DeliveryManagerRouter;