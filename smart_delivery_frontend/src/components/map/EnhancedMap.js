import React, { useState, useEffect } from 'react';
import {
    MapContainer,
    TileLayer,
    Marker,
    Popup,
    useMap,
    useMapEvents
} from 'react-leaflet';
import L from 'leaflet';
import 'leaflet-routing-machine';

const createHubIcon = () => {
    return L.divIcon({
        className: 'custom-hub-icon',
        html: `
            <div style="
                width: 30px;
                height: 30px;
                background-color: #FF9800;
                border-radius: 50%;
                border: 2px solid white;
                box-shadow: 0 2px 5px rgba(0,0,0,0.3);
                display: flex;
                justify-content: center;
                align-items: center;
                color: white;
                font-weight: bold;
            ">
                🏠
            </div>
        `,
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    });
};

const createCustomIcon = (isNextPoint) => {
    return L.divIcon({
        className: 'custom-icon',
        html: `
            <div style="position: relative; ${isNextPoint ? 'font-weight: bold;' : ''}">
                <div style="
                    width: 25px;
                    height: 25px;
                    background-color: ${isNextPoint ? '#4CAF50' : '#1976D2'};
                    border-radius: 50%;
                    border: 2px solid white;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.3);
                "></div>
                ${isNextPoint ? '<div style="position: absolute; top: -52px; left: 50%; transform: translateX(-50%); background: #4CAF50; color: white; padding: 2px 6px; border-radius: 3px; font-size: 12px; white-space: nowrap;">Điểm tiếp theo</div>' : ''}
            </div>
        `,
        iconSize: [25, 25],
        iconAnchor: [12, 12]
    });
};

// Location and Routing Control Component
function LocationAndRoutingControl({ points, nextPointIndex, showReturnRoute, hub }) {
    const [position, setPosition] = useState(null);
    const [routingControl, setRoutingControl] = useState(null);
    const [isRoutingActive, setIsRoutingActive] = useState(false);
    const [isCompleteRouteActive, setIsCompleteRouteActive] = useState(false);

    const map = useMap();

    const handleLocationFound = (e) => {
        setPosition(e.latlng);
        map.flyTo(e.latlng, 15);

        if (points && points.length > 0 && (isRoutingActive || isCompleteRouteActive)) {
            if (isCompleteRouteActive) {
                showCompleteRoute(e.latlng);
            } else {
                updateRoute(e.latlng);
            }
        }
    };

    const updateRoute = (currentPosition) => {
        if (routingControl && map) {
            map.removeControl(routingControl);
        }

        let waypoints = [];

        if (nextPointIndex === null) {
            // When nextOrder = null, show route back to hub
            waypoints = [
                L.latLng(currentPosition.lat, currentPosition.lng),
                L.latLng(hub.latitude, hub.longitude)
            ];
        } else if (points[nextPointIndex]) {
            // Otherwise show route to next point
            waypoints = [
                L.latLng(currentPosition.lat, currentPosition.lng),
                L.latLng(points[nextPointIndex].lat, points[nextPointIndex].lng)
            ];
        }

        const control = L.Routing.control({
            waypoints: waypoints,
            routeWhileDragging: true,
            showAlternatives: true,
            lineOptions: {
                styles: [{
                    color: showReturnRoute ? '#FF4444' : '#6FA1EC',
                    weight: 4
                }]
            },
            router: L.Routing.osrmv1({
                serviceUrl: 'https://router.project-osrm.org/route/v1'
            })
        });

        control.addTo(map);
        setRoutingControl(control);
    };

    const showCompleteRoute = (currentPosition) => {
        if (routingControl && map) {
            map.removeControl(routingControl);
        }

        if (!points || points.length === 0) return;

        // Create waypoints starting with current position
        let waypoints = [L.latLng(currentPosition.lat, currentPosition.lng)];

        // Sort points by their sequence if available
        const sortedPoints = [...points].sort((a, b) => {
            if (a.sequence && b.sequence) {
                return a.sequence - b.sequence;
            }
            return 0;
        });

        // Add all points to waypoints
        sortedPoints.forEach(point => {
            waypoints.push(L.latLng(point.lat, point.lng));
        });

        // Add hub as the final destination if requested
        if (hub && showReturnRoute) {
            waypoints.push(L.latLng(hub.latitude, hub.longitude));
        }

        const control = L.Routing.control({
            waypoints: waypoints,
            routeWhileDragging: false,
            showAlternatives: false,
            lineOptions: {
                styles: [{
                    color: '#9C27B0', // Purple for complete route
                    weight: 4,
                    opacity: 0.8
                }]
            },
            router: L.Routing.osrmv1({
                serviceUrl: 'https://router.project-osrm.org/route/v1'
            })
        });

        control.addTo(map);
        setRoutingControl(control);
    };

    const handleLocationError = (e) => {
        alert("Không thể tìm thấy vị trí của bạn. Vui lòng bật dịch vụ vị trí.");
    };

    useMapEvents({
        locationfound: handleLocationFound,
        locationerror: handleLocationError,
    });

    const handleLocateClick = () => {
        map.locate({ setView: false });
    };

    const toggleRouting = () => {
        setIsCompleteRouteActive(false);

        if (isRoutingActive) {
            // Turn off routing
            if (routingControl) {
                map.removeControl(routingControl);
                setRoutingControl(null);
            }
        } else {
            // Turn on routing
            if (position) {
                updateRoute(position);
            } else {
                map.locate({ setView: false });
            }
        }
        setIsRoutingActive(!isRoutingActive);
    };

    const toggleCompleteRoute = () => {
        setIsRoutingActive(false);

        if (isCompleteRouteActive) {
            // Turn off complete route
            if (routingControl) {
                map.removeControl(routingControl);
                setRoutingControl(null);
            }
        } else {
            // Turn on complete route
            if (position) {
                showCompleteRoute(position);
            } else {
                map.locate({ setView: false });
            }
        }
        setIsCompleteRouteActive(!isCompleteRouteActive);
    };

    return (
        <>
            <div className="leaflet-bottom leaflet-left" style={{ marginBottom: "20px", marginLeft: "10px" }}>
                <button
                    className="leaflet-control"
                    onClick={handleLocateClick}
                    style={{
                        padding: "6px 10px",
                        backgroundColor: "white",
                        border: "2px solid rgba(0,0,0,0.2)",
                        borderRadius: "4px",
                        cursor: "pointer",
                        marginBottom: "5px"
                    }}
                >
                    📍 Vị trí của tôi
                </button>
                <button
                    className="leaflet-control"
                    onClick={toggleRouting}
                    style={{
                        padding: "6px 10px",
                        backgroundColor: isRoutingActive ? "#FF9800" : "white",
                        border: "2px solid rgba(0,0,0,0.2)",
                        borderRadius: "4px",
                        cursor: "pointer",
                        marginBottom: "5px"
                    }}
                >
                    🧭 Chỉ đường
                </button>
                <button
                    className="leaflet-control"
                    onClick={toggleCompleteRoute}
                    style={{
                        padding: "6px 10px",
                        backgroundColor: isCompleteRouteActive ? "#9C27B0" : "white",
                        color: isCompleteRouteActive ? "white" : "black",
                        border: "2px solid rgba(0,0,0,0.2)",
                        borderRadius: "4px",
                        cursor: "pointer"
                    }}
                >
                    🚚 Chỉ đường qua tất cả điểm
                </button>
            </div>
            {position && (
                <Marker
                    position={position}
                    icon={L.divIcon({
                        className: 'custom-icon',
                        html: `<div style="width: 25px; height: 25px; background-color: #FF4444; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"></div>`,
                        iconSize: [25, 25],
                        iconAnchor: [12, 12]
                    })}
                >
                    <Popup>Vị trí của bạn</Popup>
                </Marker>
            )}
        </>
    );
};

export function EnhancedMap({ points, assignments, onNextOrder, nextOrder, hub }) {
    const [nextPointIndex, setNextPointIndex] = useState(0);
    const [showReturnRoute, setShowReturnRoute] = useState(false);

    useEffect(() => {
        if (nextOrder === null && hub) {
            // If there's no next order, show route back to hub
            setShowReturnRoute(true);
            setNextPointIndex(null);
        } else if (nextOrder && assignments) {
            setShowReturnRoute(false);
            const index = assignments.findIndex(order => order?.id === nextOrder?.id);
            if (index !== -1) {
                setNextPointIndex(index);
            }
        }
    }, [nextOrder, assignments, hub]);

    if (!points || points.length === 0) {
        return <div>Không có điểm đến nào</div>;
    }

    const formatDateTime = (dateTimeString) => {
        return new Date(dateTimeString).toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    return (
        <MapContainer
            center={nextPointIndex !== null ? [points[nextPointIndex].lat, points[nextPointIndex].lng] : [hub.latitude, hub.longitude]}
            zoom={12}
            style={{
                width: "100%",
                height: "100%"
            }}
        >
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {hub && (
                <Marker
                    position={[hub.latitude, hub.longitude]}
                    icon={createHubIcon()}
                >
                    <Popup>
                        <div style={{
                            minWidth: "200px",
                            padding: "5px"
                        }}>
                            <h3 style={{ margin: "0 0 10px 0", color: "#FF9800" }}>🏠 Hub của bạn</h3>
                            <p style={{ margin: "5px 0" }}><strong>Tên hub:</strong> {hub.name}</p>
                            <p style={{ margin: "5px 0" }}><strong>Địa chỉ:</strong> {hub.address}</p>
                        </div>
                    </Popup>
                </Marker>
            )}
            {points.map((point, index) => (
                <Marker
                    key={index}
                    position={[point.lat, point.lng]}
                    icon={createCustomIcon(index === nextPointIndex)}
                >
                    <Popup>
                        <div style={{
                            minWidth: "200px",
                            padding: "5px"
                        }}>
                            <h3 style={{
                                margin: "0 0 10px 0",
                                color: index === nextPointIndex ? '#4CAF50' : '#1976D2'
                            }}>
                                {index === nextPointIndex
                                    ? '📍 Điểm tiếp theo'
                                    : `Điểm dừng ${index + 1}`}
                            </h3>
                            <div style={{ fontSize: "14px" }}>
                                <p style={{ margin: "5px 0" }}><strong>Mã đơn:</strong> {assignments[index]?.orderId}</p>
                                <p style={{ margin: "5px 0" }}><strong>Người gửi:</strong> {assignments[index]?.senderName}</p>
                                <p style={{ margin: "5px 0" }}><strong>SĐT:</strong> {assignments[index]?.senderPhone}</p>
                                <p style={{ margin: "5px 0" }}><strong>Địa chỉ:</strong> {assignments[index]?.senderAddress}</p>
                                <p style={{ margin: "5px 0" }}><strong>Thời gian tạo:</strong> {formatDateTime(assignments[index]?.orderCreatedAt)}</p>
                                <p style={{ margin: "5px 0" }}><strong>Trạng thái:</strong> {assignments[index]?.status}</p>
                            </div>
                        </div>
                    </Popup>
                </Marker>
            ))}
            <LocationAndRoutingControl
                points={points}
                nextPointIndex={nextPointIndex}
                showReturnRoute={showReturnRoute}
                hub={hub}
            />
        </MapContainer>
    );
}