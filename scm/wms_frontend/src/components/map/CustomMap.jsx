import "leaflet/dist/leaflet.css";
import { useCallback, useMemo, useRef, useState } from "react";
import {
  MapContainer,
  Marker,
  Popup,
  TileLayer,
  useMapEvent,
} from "react-leaflet";

const googleTileLayerUrl = "https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
const googleAttribution = "Map data © Google";

function DraggableMarker({ currPos, getPos }) {
  const [draggable, setDraggable] = useState(false);
  const [position, setPosition] = useState({
    lat: currPos?.coordinates?.lat,
    lng: currPos?.coordinates?.lng,
  });
  const map = useMapEvent({
    click() {
      map.locate();
    },
    locationfound(e) {
      setPosition(e.latlng);
      map.flyTo(e.latlng, map.getZoom());
    },
  });
  const markerRef = useRef(null);
  const eventHandlers = useMemo(
    () => ({
      dragend() {
        const marker = markerRef.current;
        if (marker != null) {
          setPosition(marker.getLatLng());
        }
      },
    }),
    []
  );
  const toggleDraggable = useCallback(() => {
    setDraggable((d) => !d);
  }, []);

  getPos(position);
  return (
    <Marker
      draggable={draggable}
      eventHandlers={eventHandlers}
      position={position}
      ref={markerRef}
    >
      <Popup minWidth={90}>
        <span onClick={toggleDraggable}>
          {draggable
            ? "Marker is draggable"
            : "Click here to make marker draggable"}
        </span>
      </Popup>
    </Marker>
  );
}

const CustomMap = ({ value, onChange, style, location, mapRef }) => {
  return (
    <MapContainer
      center={{ lat: 20.991322, lng: 105.839077 }}
      zoom={13}
      scrollWheelZoom={true}
      style={{
        width: "calc(100vw - 250px)",
        height: "calc(100vh - 64px)",
        marginLeft: "-16px",
        ...style,
      }}
    >
      <TileLayer
        url={googleTileLayerUrl}
        attribution={googleAttribution}
        subdomains={["mt0", "mt1", "mt2", "mt3"]}
        // url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=yI7HfCmy5qll4mYkkO16"
        // attribution='<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>'
      />
      <DraggableMarker
        currPos={location}
        getPos={(currLoc) => {
          onChange(currLoc);
        }}
      />
    </MapContainer>
  );
};
export default CustomMap;
