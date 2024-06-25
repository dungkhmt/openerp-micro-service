import React, { useState, useEffect, FC, RefObject, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, useMap, useMapEvents, GeoJSON, Polyline, Tooltip } from 'react-leaflet';
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
    pickupPoints: IRidePickupPointHistory[];
    manipulatePickupPointsDirections: IDirectionsGetResponse | undefined;
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

export default function MapAdminHistory(
    {
        pickupPoints,
        manipulatePickupPointsDirections,
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
                    position={{ lat: pickupPointTable?.latitude, lng: pickupPointTable?.longitude }} //todo: add detail onclick later when having enough data from client flow
                    icon={
                        pickupPointTable?.address === 'SCHOOL' ? schoolIcon
                            : manipulateLocationIcon
                    }
                    eventHandlers={{
                        click: () => {

                        }
                    }}
                >
                    {/* tooltip for address when hover */}
                    <Tooltip>
                        <h3 className='font-bold'>{pickupPointTable.orderIndex}</h3>
                        {pickupPointTable?.address}
                    </Tooltip>
                </Marker>
            ))}

            <ChangeView coords={center} />

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