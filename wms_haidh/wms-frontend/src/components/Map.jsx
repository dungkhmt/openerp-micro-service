import React, { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

const Map = ({ route, enableSelection = false, onSelectLocation, selectedCoordinates, shouldAutoPan, markerCoordinates = [] }) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const routeLayerRef = useRef(null);
  const markerRef = useRef(null);
  const markersRef = useRef([]);

  useEffect(() => {
    if (!mapInstanceRef.current) {
      // Khởi tạo bản đồ
      mapInstanceRef.current = L.map(mapRef.current).setView(
        [21.027860926861657, 105.83424375610353],
        15
      );

      // Thêm lớp gạch (tile layer)
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "© OpenStreetMap contributors",
      }).addTo(mapInstanceRef.current);
    }

    const map = mapInstanceRef.current;

    // Hiển thị tuyến đường nếu có
    if (route && route.length > 0) {
      if (routeLayerRef.current) {
        routeLayerRef.current.remove();
      }
      routeLayerRef.current = L.polyline(route, {
        color: "blue",
      });
      routeLayerRef.current.addTo(map);
      map.fitBounds(routeLayerRef.current.getBounds());
    }

    // Cho phép chọn vị trí bằng cách click
    if (enableSelection) {
      map.on("click", (e) => {
        const { lat, lng } = e.latlng;

        // Xóa marker cũ nếu có
        if (markerRef.current) {
          markerRef.current.remove();
        }

        // Thêm marker mới
        markerRef.current = L.marker([lat, lng]).addTo(map);

        // Gọi callback để gửi tọa độ ra ngoài
        if (onSelectLocation) {
          onSelectLocation({ lat, lng });
        }
      });
    }

    return () => {
      map.off("click"); // Cleanup event listener khi unmount
    };
  }, [route, enableSelection, onSelectLocation]);

  useEffect(() => {
    const map = mapInstanceRef.current;
    if (!map || !selectedCoordinates) return;

    const { lat, lng } = selectedCoordinates;

    if (markerRef.current) {
      markerRef.current.remove();
    }

    markerRef.current = L.marker([lat, lng]).addTo(map);

    if (shouldAutoPan) {
      map.setView([lat, lng], 16); // ❗chỉ zoom nếu là vị trí hiện tại
    }
  }, [selectedCoordinates, shouldAutoPan]);

  useEffect(() => {
    const map = mapInstanceRef.current;
    if (!map) return;

    markersRef.current.forEach(marker => marker.remove());
    markersRef.current = [];

    markerCoordinates.forEach(({ lat, lng }) => {
      const marker = L.marker([lat, lng]).addTo(map);
      markersRef.current.push(marker);
    });
  }, [markerCoordinates]);




  return <div ref={mapRef} style={{ width: "100%", height: "400px" }} />;
};

export default Map;
