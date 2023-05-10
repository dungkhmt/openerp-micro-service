import { createControlComponent } from "@react-leaflet/core";
import L from "leaflet";
import "leaflet-routing-machine";

const createRoutineMachineLayer = (props) => {
    const {tripItems} = this.props;
    console.log("tripItems===", tripItems)
    const instance = L.Routing.control({
      waypoints: [
        L.latLng(21.018172, 105.829754),
        L.latLng(21.028172, 105.729754)
      ],
      lineOptions: {
        styles: [{ color: "#6FA1EC", weight: 4 }]
      },
      show: false,
      addWaypoints: false,
      routeWhileDragging: true,
      draggableWaypoints: true,
      fitSelectedRoutes: true,
      showAlternatives: false
    });
  
    return instance;
  };


const RoutingMachine = createControlComponent(createRoutineMachineLayer);

export default RoutingMachine;
