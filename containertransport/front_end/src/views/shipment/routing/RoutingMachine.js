import { createControlComponent } from "@react-leaflet/core";
import L, { map } from "leaflet";
import "leaflet-routing-machine";

const createRoutineMachineLayer = (props) => {
    // const {tripItems} = this.props;
    
    const { waypoints } = props;
    console.log("tripItems===", waypoints)
    // let point = [];
    // props.tripItems?.forEach((item) => {
    //   point.push(L.latLng(item?.latitude, item?.longitude))
    // })
    const icon = L.icon({
      iconUrl:
        "https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png",
      iconSize: [40, 40]
    });
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
      routeWhileDragging: false,
      draggableWaypoints: false,
      fitSelectedRoutes: false,
      showAlternatives: false
    });
  
    return instance;
  };


const RoutingMachine = createControlComponent(createRoutineMachineLayer);

export default RoutingMachine;
