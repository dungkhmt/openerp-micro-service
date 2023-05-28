import { Typography } from "@mui/material";
import L, { icon } from "leaflet";
import "leaflet-routing-machine";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import "leaflet-routing-machine/dist/leaflet.routing.icons.png";
import { useRef, useState } from "react";
import {
  MapContainer,
  Marker,
  Polyline,
  Popup,
  TileLayer,
} from "react-leaflet";
import { useLocation } from "react-router-dom";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import RoutingMachine from "./RoutingMachine";
const googleTileLayerUrl = "https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
const googleAttribution = "Map data © Google";
const shopIcon = icon({
  iconUrl: require("../../../assets/images/shop2.png"),
  iconSize: [40, 40], // set the size of the icon
  iconAnchor: [16, 0], // set the anchor point of the icon
});
const facilityIcon = icon({
  iconUrl: require("../../../assets/images/inventory3.png"),
  iconSize: [40, 40], // set the size of the icon
  iconAnchor: [16, 0], // set the anchor point of the icon
});
function TripRoutesScreen({ screenAuthorization }) {
  const location = useLocation();
  const tripRoute = location.state.tripRoute;
  const markerRef = useRef(null);
  const [waypoints, setWaypoints] = useState(
    tripRoute?.truckRoute?.routeElements?.map((cus) => {
      return L.latLng(cus?.node?.x, cus?.node?.y);
    })
  );
  const polyPoints = tripRoute?.droneRoutes
    ?.map((droneRoute) => {
      return droneRoute.droneRouteElements?.map((cus, index) => {
        return [cus?.node?.x, cus?.node?.y];
      });
    })
    .flat();
  return (
    <MapContainer
      center={{ lat: 21.008330038713357, lng: 105.84273632066207 }}
      zoom={13}
      scrollWheelZoom={true}
      style={{
        width: "calc(100vw - 250px)",
        height: "calc(100vh - 64px)",
        marginLeft: "-16px",
      }}
    >
      <TileLayer
        url={googleTileLayerUrl}
        attribution={googleAttribution}
        subdomains={["mt0", "mt1", "mt2", "mt3"]}
        // url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=yI7HfCmy5qll4mYkkO16"
        // attribution='<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>'
      />
      {tripRoute?.truckRoute?.routeElements?.map((cus, index) => (
        <Marker
          key={index}
          position={[cus?.node?.x, cus?.node?.y]}
          icon={cus?.node?.name === "depot" ? facilityIcon : shopIcon}
          ref={markerRef}
        >
          <Popup>
            <Typography variant="h6"> {cus?.node?.name}</Typography>
            <Typography variant="body2">{cus?.phone} </Typography>
          </Popup>
        </Marker>
      ))}
      {tripRoute?.droneRoutes?.map((droneRoute) => {
        return droneRoute.droneRouteElements?.map((cus, index) => (
          <Marker
            key={index}
            position={[cus?.node?.x, cus?.node?.y]}
            icon={cus?.node?.name === "depot" ? facilityIcon : shopIcon}
            ref={markerRef}
          >
            <Popup>
              <Typography variant="h6"> {cus?.node?.name}</Typography>
              <Typography variant="body2">{cus?.phone} </Typography>
            </Popup>
          </Marker>
        ));
      })}
      <Polyline
        positions={[polyPoints ? polyPoints : []]}
        pathOptions={{ color: "red" }}
      />
      <RoutingMachine waypoints={waypoints} />
    </MapContainer>
  );
}
const SCR_ID = "SCR_TRIP_ROUTE";
export default withScreenSecurity(TripRoutesScreen, SCR_ID, true);
