import { Routes, Route } from "react-router-dom";
import DeliveryTripDetail from "../views/delivery_manager/delivery_trip/DeliveryTripDetail";
import DeliveryTrip from "../views/delivery_manager/delivery_trip/DeliveryTripList";
import AddTrip from "../views/delivery_manager/delivery_trip/AddTrip";
import DeliveryTripItem from "../views/delivery_manager/delivery_trip/DeliveryTripItem";
import DeliveryPerson from "../views/delivery_manager/delivery_person/DeliveryPersonList";
import Shipment from "../views/delivery_manager/shipment/ShipmentList";
import ShipmentDetail from "../views/delivery_manager/shipment/ShipmentDetail";
import Distance from "../views/delivery_manager/distance/DistanceList";
import RoutingRecommendation from "../views/delivery_manager/shipment/RoutingRecommendation";
import RoutingResult from "../views/delivery_manager/shipment/RoutingResult";

function DeliveryManagerRouter() {
  return (
    <Routes>   
      <Route path="delivery-person" element={<DeliveryPerson />} />
      <Route path="delivery-person/:id" element={<DeliveryPerson />} />
      <Route path="shipments" element={<Shipment />} />
      <Route path="shipments/:id" element={<ShipmentDetail />} />
      <Route path="shipments/:id/auto-routing" element={<RoutingRecommendation />} />
      <Route path="shipments/:id/auto-routing/result" element={<RoutingResult />} />
      <Route path="delivery-trip" element={<DeliveryTrip />} />
      <Route path="delivery-trip/add-trip" element={<AddTrip />} /> 
      <Route path="delivery-trip/:id" element={<DeliveryTripDetail/>} />
      <Route path="delivery-trip/:id1/:id2" element={<DeliveryTripItem/>} />   
      <Route path="distances" element={<Distance/>} />   
    </Routes>
  );
}

export default DeliveryManagerRouter;