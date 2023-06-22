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
      // createMarker: function (i, start, n){
      //   var marker_icon = null
      //   if (i == 0) {
      //       // This is the first marker, indicating start
      //       marker_icon = icon
      //   } else if (i == n -1) {
      //       //This is the last marker indicating destination
      //       marker_icon = icon
      //   }
      //   var marker = L.marker (start.latLng, {
      //               draggable: true,
      //               bounceOnAdd: false,
      //               bounceOnAddOptions: {
      //                   duration: 1000,
      //                   height: 800, 
      //                   // function(){
      //                   //     (bindPopup(myPopup).openOn(map))
      //                   // }
      //               },
      //               icon: marker_icon
      //   })
      //   return marker
      // },
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
