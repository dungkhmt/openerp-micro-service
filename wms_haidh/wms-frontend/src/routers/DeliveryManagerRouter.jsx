import { Routes, Route } from "react-router-dom";
import DeliveryTripDetail from "../views/delivery_manager/DeliveryTripDetail";
import DeliveryTrip from "../views/delivery_manager/DeliveryTrip";
import AddTrip from "../views/delivery_manager/AddTrip";
import DeliveryPerson from "../views/delivery_manager/DeliveryPerson";
import Shipment from "../views/delivery_manager/Shipment";
import DeliveryTripItem from "../views/delivery_manager/DeliveryTripItem";
function DeliveryManagerRouter() {
  return (
    <Routes>
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/add-trip" element={<AddTrip />} /> 
      <Route path="delivery-trip/:id1" element={<DeliveryTripDetail/>} />
      <Route path="delivery-trip/:id1/:id2" element={<DeliveryTripItem/>} />   
      <Route path="delivery-person" element={<DeliveryPerson />} />
      <Route path="shipments" element={<Shipment />} />
    </Routes>
  );
}

export default DeliveryManagerRouter;