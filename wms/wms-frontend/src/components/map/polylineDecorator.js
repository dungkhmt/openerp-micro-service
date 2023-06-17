import { useEffect } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-polylinedecorator";
import "leaflet-arrowheads";

const ArrowPolyline = ({ patterns, polyline }) => {
  // https://codesandbox.io/s/leaflet-arrowheads-example-forked-vznslv?file=/package.json
  const map = useMap();

  useEffect(() => {
    if (!map) return;
    var path = L.polyline(polyline, { smoothFactor: 1.5, weight: 2 })
    .arrowheads()
    .bindPopup(`<pre><code>L.polyline(coords).arrowheads({})</code></pre>`, {
      maxWidth: 2000,
      minWidth: 400
    });  
    var group = L.layerGroup([path]);
    group.addTo(map);
  }, [map]);

  return null;
}

export default ArrowPolyline;