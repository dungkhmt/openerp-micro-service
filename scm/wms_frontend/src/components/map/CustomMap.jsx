import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { useEffect, useMemo, useRef } from "react";
import {
  MapContainer,
  Marker,
  Popup,
  TileLayer,
  useMap,
  useMapEvent,
} from "react-leaflet";
const googleTileLayerUrl = "https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
const googleAttribution = "Map data © Google";

function DraggableMarker({ currPos, setSelectPosition }) {
  const markerRef = useRef(null);
  const map = useMapEvent({
    click() {
      map.locate();
    },
    locationfound(e) {
      setSelectPosition(e.latlng);
      map.flyTo(e.latlng, map.getZoom());
    },
  });
  const eventHandlers = useMemo(
    () => ({
      dragend() {
        const marker = markerRef.current;
        if (marker != null) {
          setSelectPosition(marker.getLatLng());
        }
      },
    }),
    []
  );
  return (
    <Marker
      draggable={true}
      eventHandlers={eventHandlers}
      position={currPos}
      ref={markerRef}
    >
      <Popup minWidth={90}></Popup>
    </Marker>
  );
}

function ResetCenterView(props) {
  const { selectPosition, setSelectPosition } = props;
  const map = useMap();

  useEffect(() => {
    if (selectPosition) {
      map.setView(
        L.latLng(selectPosition?.lat, selectPosition?.lng),
        map.getZoom(),
        {
          animate: true,
        }
      );
      setSelectPosition(selectPosition);
    }
  }, [selectPosition]);

  return null;
}
const CustomMap = ({ style, location, mapRef, setSelectPosition }) => {
  const locationSelection = [location?.lat, location?.lng];
  return (
    <MapContainer
      center={{ lat: 20.991322, lng: 105.839077 }}
      zoom={13}
      scrollWheelZoom={true}
      ref={mapRef}
      style={{
        // width: "calc(100vw - 250px)",
        // height: "calc(100vh - 64px)",
        // marginLeft: "-16px",
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
      {location && (
        <DraggableMarker
          currPos={locationSelection}
          setSelectPosition={setSelectPosition}
        />
      )}
      <ResetCenterView
        selectPosition={location}
        setSelectPosition={setSelectPosition}
      />
    </MapContainer>
  );
};
export default CustomMap;
