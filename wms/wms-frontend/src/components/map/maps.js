import React, { useEffect, useState } from "react";
import { MapContainer, Marker, Polyline, Popup, TileLayer, useMap, useMapEvents } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import { PLACE_HOLDER_ICON_URL, BLUE_PLACE_HOLDER_ICON_URL } from "components/constants";
import "leaflet-polylinedecorator";
import "leaflet-polylinedecorator";
import ArrowPolyline from "./polylineDecorator";

const icon = L.icon({
  iconUrl: PLACE_HOLDER_ICON_URL,
  iconSize: [38, 38],
});

const iconWithNumber = [];
const LARGEST_ICON_NUMBER = 20; 
for (var i = 1; i <= LARGEST_ICON_NUMBER; i++) {
  iconWithNumber.push(L.icon({
    iconUrl: `../../../../../../../placeholder-with-number/${i}.png`,
    iconSize: [38, 38],
  }))
}

const getIconBySequence = ( sequence ) => {
  if (sequence == undefined || sequence == null || sequence < 1 || sequence > 20) {
    return icon;
  }
  return iconWithNumber[sequence - 1];
}

const bluePlaceHolder = L.icon({
  iconUrl: BLUE_PLACE_HOLDER_ICON_URL,
  iconSize: [38, 38],
});

const NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse?";
const TILE_LAYER_URL = "https://api.maptiler.com/maps/basic/256/{z}/{x}/{y}.png?key=GsqVDsxlKcMfyPpnz8xW";
const TILE_LAYER_ATTRIBUTE = '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';

/**
 * This method return address in String of selected latitude and longtitude
 * @param {*} lat 
 * @param {*} lon 
 * @returns 
 */
const reverse = async (lat, lon) => {
  // fetch(NOMINATIM_REVERSE_URL + "lat=" + lat + "&lon=" + lon + "&format=json");
  const response = await fetch(NOMINATIM_REVERSE_URL + new URLSearchParams({lat: lat, lon: lon, format: 'json'}), {method: 'GET'});
  const json = await response.json();
  return json;
}

const position = [21, 105]; // [lat, lon]

function ResetCenterView(props) {
  const { selectPosition } = props;
  const map = useMap();

  useEffect(() => {
    if (selectPosition) {
      map.setView(
        L.latLng(selectPosition?.lat, selectPosition?.lon),
        map.getZoom(),
        {
          animate: true
        }
      )
    }
  }, [selectPosition]);

  return null;
}

export default function Maps(props) {
  const { selectPosition, setSelectPosition } = props;
  const [locationSelection, setLocationSelection] = useState([21.0294498, 105.8544441]);

  useEffect(() => {
    if (selectPosition !== null) {
      console.log("Selected position => ", parseFloat(selectPosition.lat), parseFloat(selectPosition.lon));
      setLocationSelection([parseFloat(selectPosition.lat), parseFloat(selectPosition.lon)]);
    }
  }, [selectPosition]);

  const LocationFinderDummy = () => {
    const map = useMapEvents({
      async click(e) {
        console.log(e.latlng);
        setSelectPosition({lat: e.latlng.lat, lon: e.latlng.lng});
        const newPosition = await reverse(e.latlng.lat, e.latlng.lng);
        setSelectPosition(newPosition);
      },
    });
    return null;
  };

  return (
    <MapContainer
      center={position}
      zoom={8}
      style={{ width: "100%", height: "100%" }}
    >
      <LocationFinderDummy />
      <TileLayer
        attribution={TILE_LAYER_ATTRIBUTE}
        url={TILE_LAYER_URL}
      />
      {selectPosition && (
        <Marker position={locationSelection} icon={icon}>
          <Popup>
            A pretty CSS3 popup. <br /> Easily customizable.
          </Popup>
        </Marker>
      )}
      <ResetCenterView selectPosition={selectPosition} />
    </MapContainer>
  );
}

export function RouteMap ({ points, customers, warehouse }) {
  const arrow = [{
    offset: 25,
    repeat: 50,
    symbol: L.Symbol.arrowHead({
      pixelSize: 15,
      pathOptions: {
        fillOpacity: 1,
        weight: 0
      }
    })
  }];
  return (
    <MapContainer
      center={position}
      zoom={8}
      style={{ width: "100%", height: "100%" }}
    >
      <TileLayer
        attribution={TILE_LAYER_ATTRIBUTE}
        url={TILE_LAYER_URL}
      />
      <ArrowPolyline patterns={arrow} polyline={points} />
      {
        customers != null && customers != undefined &&
        customers.length > 0 &&
        customers.map(customer => <Marker position={customer.position} icon={getIconBySequence(customer?.sequence)}>
                                    <Popup>
                                      {customer.name}
                                    </Popup>
                                  </Marker>)
      }
      {
        warehouse != null && warehouse != undefined &&
        <Marker position={warehouse.position} icon={bluePlaceHolder}>
          <Popup>
            {warehouse.name}
          </Popup>
        </Marker>
      }
    </MapContainer>
  );
}

export function ListingMaps( props ) {
  const { warehouses } = props;
  const center = [Math.max(...warehouses.map(w => w.latitude)), 
    Math.max(...warehouses.map(w => w.longitude))]; // TODO: calculate center points by warehouses location

  return (
    <MapContainer
      center={center}
      zoom={8}
      style={{ width: "100%", height: "100%" }}
    >
      <TileLayer
        attribution={TILE_LAYER_ATTRIBUTE}
        url={TILE_LAYER_URL}
      />
      {
        warehouses.length > 0 &&
        warehouses.map((warehouse) => {
          console.log("Warehouse -> ", warehouse);
          return (
            <Marker 
              position={[ warehouse.latitude, warehouse.longitude ]}
              icon={icon}
            >
              <Popup>
                {warehouse.name}
              </Popup>
            </Marker>
          );
        })
      }
    </MapContainer>
  )
}