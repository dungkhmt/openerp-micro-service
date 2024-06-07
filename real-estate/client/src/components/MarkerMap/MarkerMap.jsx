import { MapContainer, TileLayer } from "react-leaflet";
import React, { useEffect, useState } from "react";
import "./MarkerMap.css";

import Pin from "../Pin/Pin";
import "leaflet/dist/leaflet.css";
import { v4 as uuidv4 } from "uuid";

const MarkerMap = ({ posts }) => {
  const uniqueId = uuidv4();

  const [mapLoaded, setMapLoaded] = useState(false);

  useEffect(() => {
    // Kiểm tra xem dữ liệu đã được tải hay chưa
    if (posts.length > 0 && posts[0] !== undefined) {
      setMapLoaded(true);
    }
  }, [posts]);

  return (
    <div className="map-wrapper">
      {mapLoaded ? (
        <MapContainer
          id={uniqueId}
          center={posts[0]?.position}
          // center={[10 , 105]}
          zoom={7}
          scrollWheelZoom={false}
          className="map"
          // style={{
          //     height: "100%",
          //     width: "100%",
          //     marginTop: "20px",
          //     zIndex: 999,
          // }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {posts.map((post, index) => (
            <Pin post={post} key={index} />
          ))}
        </MapContainer>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
};
export default MarkerMap;
