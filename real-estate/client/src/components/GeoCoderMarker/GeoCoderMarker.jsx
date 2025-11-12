import React, { useEffect, useMemo, useRef, useState } from "react";
import { Marker, Popup, useMap } from "react-leaflet";
import L, { Icon } from "leaflet";
import "leaflet/dist/leaflet.css";
import { apiGetPosition } from "../../services/AppRequest";

const customIcon = new Icon({
  iconUrl: require("../../images/placeholder.png"),
  iconSize: [20, 20],
});

const GeoCoderMarker = ({ address, setPosition }) => {
  const map = useMap();
  const [positionMarker, setPositionMarker] = useState([21, 105]);
  useEffect(() => {
    const fetchData = async () => {
      const response = await apiGetPosition(address);
      if (response.status === 200) {
        const lng = response?.data.features[0].center[0];
        const lat = response?.data.features[0].center[1];
        setPosition([lat, lng]);
        setPositionMarker([lat, lng]);
        map.flyTo([lat, lng], 12);
      }
    };

    if (address.length > 12) {
      fetchData();
    }
  }, [address]);
  const markerRef = useRef(null);
  const eventHandlers = useMemo(
    () => ({
      dragend() {
        const marker = markerRef.current;
        if (marker != null) {
          const positionMaker = marker.getLatLng();
          console.log(positionMaker);
          setPosition([positionMaker.lat, positionMaker.lng]);
        }
      },
    }),
    [],
  );
  return (
    <Marker
      draggable={true}
      eventHandlers={eventHandlers}
      ref={markerRef}
      position={positionMarker}
      icon={customIcon}
    >
      <Popup />
    </Marker>
  );
};

export default GeoCoderMarker;
