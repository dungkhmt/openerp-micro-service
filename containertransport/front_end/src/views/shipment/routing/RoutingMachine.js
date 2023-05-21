import { createControlComponent } from "@react-leaflet/core";
import L from "leaflet";
import "leaflet-routing-machine";

const createRoutineMachineLayer = (props) => {
    // const {tripItems} = this.props;
    
    const { waypoints } = props;
    console.log("tripItems===", waypoints)
    // let point = [];
    // props.tripItems?.forEach((item) => {
    //   point.push(L.latLng(item?.latitude, item?.longitude))
    // })
    const instance = L.Routing.control({
      waypoints: waypoints,
      // [
      //   L.latLng(21.018172, 105.829754),
      //   L.latLng(21.028172, 105.729754),
      //   L.latLng(21.10897, 105.799789)
      // ],
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
