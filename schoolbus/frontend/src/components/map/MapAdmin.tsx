import React, { useState, useEffect, FC, RefObject, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, useMap, useMapEvents, GeoJSON, Polyline, Tooltip, Popup } from 'react-leaflet';
import L, { LatLngExpression, LatLngTuple } from 'leaflet';
import polyline from 'polyline';
// import { PolylineDecorator } from 'leaflet-polylinedecorator';
import 'leaflet-polylinedecorator';

const locationIcon = L.icon({
    iconUrl: 'https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png',
    iconSize: [38, 38],
});

const manipulateLocationIcon = L.icon({
    iconUrl: 'https://cdn4.iconfinder.com/data/icons/BRILLIANT/transportation/png/256/school_bus.png',
    iconSize: [38, 38],
});

const schoolIcon = L.icon({
    iconUrl: 'https://cdn4.iconfinder.com/data/icons/education-738/64/college-school-university-education-building-architecture-256.png',
    iconSize: [38, 38],
});

interface Coords {
    lat: number;
    lng: number;
}

interface ChangeViewProps {
    coords: Coords;
}

export const ChangeView: FC<ChangeViewProps> = ({ coords }) => {
    const map = useMap();
    useEffect(() => {
        map.setView(coords);
    }, [coords, map]);
    return null;
}

interface MapProps {
    autoCompletePoint: IFeature | null;
    pickupPoints: IPickupPointTable[];
    manipulatePickupPointsDirections: IDirectionsGetResponse | undefined;
    enableClickMap: boolean;
    manipulatePickupPoints: IPickupPoint[];
    setManipulatePickupPoints: React.Dispatch<React.SetStateAction<IPickupPoint[]>>;
}

interface ClickMapComponentProps {
    geoData: Coords;
    setGeoData: React.Dispatch<React.SetStateAction<Coords>>;
}

const ClickMapComponent: React.FC<ClickMapComponentProps> = ({ geoData, setGeoData }) => {
    useMapEvents({
        click: (e) => {
            console.log('location clicked:', e.latlng);
            setGeoData(e.latlng);
        },
    });

    return null;
}

function MapEvents() {
    const map = useMapEvents({
        zoomend: () => {
            const zoom = map.getZoom();
            map.flyTo(map.getCenter(), zoom);
        },
    });

    return null;
}

// Create a transparent icon
const transparentIcon = new L.Icon({
    iconUrl: 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7',
    iconSize: [1, 1],
    iconAnchor: [0, 0],
    popupAnchor: [0, 0],
    shadowUrl: undefined,
    shadowSize: undefined,
    shadowAnchor: undefined
});


// arrow decorator for polyline
const ArrowDecorator = ({ positions }: { positions: LatLngExpression[] }) => {
    const map = useMap();

    useEffect(() => {
        const polyline = L.polyline(positions);
        const decorator = L.polylineDecorator(polyline, {
            patterns: [
                {
                    offset: '2%',
                    repeat: '5%',
                    symbol: L.Symbol.arrowHead({
                        pixelSize: 10,
                        polygon: false,
                        pathOptions: {
                            stroke: true,
                            color: '#f54242',
                            weight: 2
                        }
                    })
                }
            ]
        }).addTo(map);

        return () => {
            map.removeLayer(decorator);
        };
    }, [map, positions]);

    return null;
};

export default function MapAdmin(
    {
        autoCompletePoint,
        pickupPoints,
        manipulatePickupPointsDirections,
        enableClickMap,
        manipulatePickupPoints,
        setManipulatePickupPoints
    }: MapProps
) {
    const zoomRef = useRef<number>(12);
    const [geoData, setGeoData] = useState<Coords>({ lat: 21.028511, lng: 105.804817 });

    const center: Coords = { lat: geoData.lat, lng: geoData.lng };

    return (
        <MapContainer
            center={center}
            zoom={12}
            style={{ height: '70vh' }}
        >
            <TileLayer
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            {/* pickup points */}
            {pickupPoints?.map((pickupPointTable, index) => (
                <Marker
                    key={index}
                    position={{ lat: pickupPointTable.pickupPoint.latitude, lng: pickupPointTable.pickupPoint.longitude }} //todo: add detail onclick later when having enough data from client flow
                    icon={
                        pickupPointTable.pickupPoint.address === 'SCHOOL' ? schoolIcon
                            : manipulatePickupPoints.some(pickupPoint => pickupPoint.id === pickupPointTable.pickupPoint.id) ? manipulateLocationIcon
                                : locationIcon
                    }
                    eventHandlers={{
                        click: () => {
                            if (manipulatePickupPoints.some(pickupPoint => pickupPoint.id === pickupPointTable.pickupPoint.id)) {
                                const index = manipulatePickupPoints.findIndex(pickupPoint => pickupPoint.id === pickupPointTable.pickupPoint.id);
                                if (manipulatePickupPoints.length > 1)
                                    setManipulatePickupPoints(manipulatePickupPoints.slice(0, index + 1));
                                else
                                    setManipulatePickupPoints([]);
                            } else {
                                setManipulatePickupPoints([...manipulatePickupPoints, pickupPointTable.pickupPoint]);
                            }
                        }
                    }}
                >
                    {/* tooltip for address when hover */}
                    <Tooltip>
                        {pickupPointTable.pickupPoint.address}
                    </Tooltip>
                </Marker>
            ))}

            {/* just the size of students at each of pickup points: pickupPointTable.students.size to add more information in map */}
            {pickupPoints?.map((pickupPointTable, index) => (
                <Marker
                    key={index}
                    position={{ lat: pickupPointTable.pickupPoint.latitude, lng: pickupPointTable.pickupPoint.longitude }}
                    icon={transparentIcon}
                >
                    <Tooltip permanent direction='left' offset={[-20, 0]}>
                        <div className="bg-white rounded shadow p-0">
                            <p className="font-bold text-blue-500">{pickupPointTable.students.length + " HS"}</p>
                        </div>
                    </Tooltip>
                </Marker>
            ))}

            {/* autoComplete point */}
            {autoCompletePoint && (
                <Marker
                    key={autoCompletePoint.properties.id}
                    position={{ lat: autoCompletePoint.geometry.coordinates[1], lng: autoCompletePoint.geometry.coordinates[0] }}
                    icon={locationIcon}
                />
            )}

            <ChangeView coords={center} />

            {enableClickMap && <ClickMapComponent geoData={geoData} setGeoData={setGeoData} />}

            {manipulatePickupPointsDirections?.routes.map((route, routeIndex) => {
                // const decodedPolyline = polyline.decode(route.geometry).map((coordinate: number[]) => [coordinate[0], coordinate[1]]);
                const decodedPolyline = polyline.decode(route.geometry).map((coordinate: number[]) => ({ lat: coordinate[0], lng: coordinate[1] }));
                const distance = (route.summary.distance / 1000).toFixed(2); // convert distance to km
                const durationInSeconds = route.summary.duration;
                const hours = Math.floor(durationInSeconds / 3600);
                const minutes = Math.floor((durationInSeconds % 3600) / 60);

                return (
                    <>
                        <Polyline
                            key={`route-border-${routeIndex}`}
                            positions={decodedPolyline}
                            color="blue" // Border color
                            weight={8} // Border width
                        />
                        <Polyline
                            key={`route-${routeIndex}`}
                            positions={decodedPolyline}
                            color="#910322"
                            weight={6}
                            eventHandlers={{
                                mouseover: (e) => {
                                    e.target.openTooltip();
                                },
                                mouseout: (e) => {
                                    e.target.closeTooltip();
                                },
                            }}
                        >
                            <Tooltip>
                                {`Khoảng cách: ${distance} km, Thời gian: ${hours > 0 ? `${hours} giờ ` : ''}${minutes} phút`}
                            </Tooltip>
                        </Polyline>
                        <ArrowDecorator positions={polyline.decode(route.geometry).map((coordinate: number[]) => [coordinate[0], coordinate[1]] as LatLngTuple)} />
                    </>

                );
            })}

            <MapEvents />
        </MapContainer>
    );
}