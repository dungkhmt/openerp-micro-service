import { Typography } from "@mui/material";
import { icon } from "leaflet";
import { useRef } from "react";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import { useLocation } from "react-router-dom";
import withScreenSecurity from "../../../components/common/withScreenSecurity";

const googleTileLayerUrl = "https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
const googleAttribution = "Map data © Google";
const customIcon = icon({
  iconUrl: require("../../../assets/images/inventory3.png"),
  iconSize: [40, 40], // set the size of the icon
  iconAnchor: [16, 0], // set the anchor point of the icon
});

function FacilityMapScreen({ screenAuthorization }) {
  const location = useLocation();
  const facility = location.state.facility;
  const markerRef = useRef(null);

  return (
    <MapContainer
      center={{ lat: 21.008330038713357, lng: 105.84273632066207 }}
      zoom={13}
      scrollWheelZoom={true}
      style={{
        width: "calc(100vw)",
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
      {facility.map((cus, index) => (
        <Marker
          key={index}
          position={[cus?.latitude, cus?.longitude]}
          icon={customIcon}
          ref={markerRef}
        >
          <Popup>
            <Typography variant="h6"> {cus?.name}</Typography>
            <Typography variant="body2">{cus?.phone} </Typography>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}
const SCR_ID = "SCR_SCM_FACILITY_MAP";
export default withScreenSecurity(FacilityMapScreen, SCR_ID, true);
