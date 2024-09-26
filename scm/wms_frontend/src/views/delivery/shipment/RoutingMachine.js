import { createControlComponent } from "@react-leaflet/core";
import L from "leaflet";
import "leaflet-routing-machine";

const createRoutineMachineLayer = ({ waypoints }) => {
  const instance = L.Routing.control({
    waypoints: [...waypoints],
    lineOptions: {
      styles: [{ color: "#6FA1EC", weight: 4 }],
    },
    show: true,
    addWaypoints: false,
    routeWhileDragging: true,
    draggableWaypoints: false,
    fitSelectedRoutes: false,
    showAlternatives: true,
  });

  return instance;
};

const RoutingMachine = createControlComponent(createRoutineMachineLayer);

export default RoutingMachine;
