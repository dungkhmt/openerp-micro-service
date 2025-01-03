import React, { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const Map = ({ route }) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const routeLayerRef = useRef(null);

  useEffect(() => {
    if (!mapInstanceRef.current) {
      // Khởi tạo bản đồ
      mapInstanceRef.current = L.map(mapRef.current).setView([21.027860926861657, 105.83424375610353], 15);

      // Thêm lớp gạch (tile layer)
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
      }).addTo(mapInstanceRef.current);
    }

    const map = mapInstanceRef.current;

    // Hiển thị tuyến đường
    if (route && route.length > 0) {
      // Xóa tuyến đường cũ nếu tồn tại
      if (routeLayerRef.current) {
        routeLayerRef.current.remove();
      }

      // Thêm tuyến đường mới
      routeLayerRef.current = L.polyline(route.map(({ lng, lat }) => [lat, lng]), { color: 'blue' });
      routeLayerRef.current.addTo(map);

      // Cập nhật vùng nhìn (fitBounds)
      map.fitBounds(routeLayerRef.current.getBounds());
    }
  }, [route]);

  return <div ref={mapRef} style={{ width: '100%', height: '400px' }} />;
};

export default Map;
