import { Typography } from "@mui/material";
import { icon } from "leaflet";
import { useRef } from "react";
import {
  MapContainer,
  Marker,
  Polyline,
  Popup,
  TileLayer,
} from "react-leaflet";
import { useLocation } from "react-router-dom";
import withScreenSecurity from "../../../components/common/withScreenSecurity";
import { generateRandomColor } from "../../../utils/GlobalUtils";

const googleTileLayerUrl = "https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
const googleAttribution = "Map data © Google";
const customIcon = icon({
  iconUrl: require("../../../assets/images/shop2.png"),
  iconSize: [40, 40], // set the size of the icon
  iconAnchor: [16, 0], // set the anchor point of the icon
});
const facIcon = icon({
  iconUrl: require("../../../assets/images/inventory3.png"),
  iconSize: [40, 40], // set the size of the icon
  iconAnchor: [16, 0], // set the anchor point of the icon
});
function CustomerMapScreen({ screenAuthorization }) {
  const location = useLocation();
  const customer = location.state.customer;
  const markerRef = useRef(null);

  const getAllFacilities = (customer) => {
    // debugger;

    const uniqueIds = [];

    const uniqueFacilities = customer?.filter((element) => {
      const isDuplicate = uniqueIds.includes(element?.facility?.id);

      if (!isDuplicate) {
        uniqueIds.push(element?.facility?.id);

        return true;
      }

      return false;
    });
    return uniqueFacilities.map((cus) => cus?.facility);
  };

  const polyPoints = customer?.map((cus, index) => {
    return [
      [
        parseFloat(cus?.facility?.latitude),
        parseFloat(cus?.facility?.longitude),
      ],
      [parseFloat(cus?.latitude), parseFloat(cus?.longitude)],
    ];
  });
  function mapColorToFacility(polyPoints) {
    const groupedMap = new Map();

    for (const [facility, customers] of polyPoints) {
      const key = JSON.stringify(facility);
      let clusterColor = generateRandomColor();
      groupedMap.set(key, clusterColor);
    }
    return groupedMap;
  }
  var facilities = getAllFacilities(customer);
  const colorByFacility = mapColorToFacility(polyPoints);
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
      {customer.map((cus, index) => (
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
      {facilities?.map((fac, index) => (
        <Marker
          key={index}
          position={[fac?.latitude, fac?.longitude]}
          icon={facIcon}
          ref={markerRef}
        >
          <Popup>
            <Typography variant="h6"> {fac?.name}</Typography>
            <Typography variant="body2">{fac?.phone} </Typography>
          </Popup>
        </Marker>
      ))}
      {polyPoints.map((points, index) => {
        return (
          <Polyline
            key={index}
            positions={[points ? points : []]}
            pathOptions={{
              color: colorByFacility.get(JSON.stringify(points[0])),
            }}
          />
        );
      })}
    </MapContainer>
  );
}
const SCR_ID = "SCR_SCM_CUSTOMER_MAP";
export default withScreenSecurity(CustomerMapScreen, SCR_ID, true);
