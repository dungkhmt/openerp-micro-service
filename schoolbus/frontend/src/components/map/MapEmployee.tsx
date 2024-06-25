import React, { useState, useEffect, FC, RefObject, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import {
    MapContainer,
    TileLayer,
    Marker,
    useMap,
    useMapEvents,
    GeoJSON,
    Polyline,
    Tooltip
} from 'react-leaflet';
import L, { LatLngExpression, LatLngTuple } from 'leaflet';
import polyline from 'polyline';
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

export const ChangeView: FC<ChangeViewProps> = ({ coords }) => {
    const map = useMap();
    useEffect(() => {
        map.setView(coords);
    }, [coords, map]);
    return null;
}

interface MapProps {
    features: IFeature[];
    directionsGetResponse: IDirectionsGetResponse | undefined;
    enableClickMap: boolean;
    manipulatePickupPointsOutput: IManipulatePickupPointOutput | undefined;
}

interface MyComponentProps {
    geoData: Coords;
    setGeoData: React.Dispatch<React.SetStateAction<Coords>>;
}

const ClickMapComponent: React.FC<MyComponentProps> = ({ geoData, setGeoData }) => {
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

export default function MapEmployee({
    features,
    directionsGetResponse,
    enableClickMap,
    manipulatePickupPointsOutput
}: MapProps) {
    const zoomRef = useRef<number>(12);
    const [geoData, setGeoData] = useState<Coords>({ lat: 21.028511, lng: 105.804817 });

    const center: Coords = { lat: geoData.lat, lng: geoData.lng };

    console.log('directions get: ', directionsGetResponse)

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
            {/* {geoData.lat && geoData.lng && (
                <Marker position={center} icon={locationIcon} />
            )} */}
            {features.map((feature, index) => (
                <Marker
                    key={index}
                    position={{ lat: feature.geometry.coordinates[1], lng: feature.geometry.coordinates[0] }}
                    icon={locationIcon}
                />
            ))}
            <ChangeView coords={center} />


            {/* enable click map */}
            {enableClickMap && (
                <ClickMapComponent geoData={geoData} setGeoData={setGeoData} />
            )}

            {
                (features.length > 0 && geoData.lat && geoData.lng) && (
                    <Polyline positions={[
                        [geoData.lat, geoData.lng],
                        [features[0].geometry.coordinates[1], features[0].geometry.coordinates[0]]
                    ]} />
                )
            }

            {/* manipulate pickup points */}
            {manipulatePickupPointsOutput?.pickupPointWithStudents?.map((pickupPointWithStudents, index) => (
                <Marker
                    key={index}
                    position={{ lat: pickupPointWithStudents.pickupPoint.latitude, lng: pickupPointWithStudents.pickupPoint.longitude }}
                    icon={
                        pickupPointWithStudents.pickupPoint.address === 'SCHOOL' ? schoolIcon
                            : manipulateLocationIcon
                    }
                >
                    <Tooltip>
                        {pickupPointWithStudents.pickupPoint.address}
                    </Tooltip>
                </Marker>
            ))}

            {/* direction */}
            {directionsGetResponse?.routes.map((route, routeIndex) => {
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