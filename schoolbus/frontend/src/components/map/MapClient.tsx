import React, { useState, useEffect, FC, RefObject, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, useMap, useMapEvents, GeoJSON, Polyline, Tooltip } from 'react-leaflet';
import L from 'leaflet';
import polyline from 'polyline';

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

const wavingPeopleIcon = L.icon({
    iconUrl: 'https://cdn2.iconfinder.com/data/icons/graduated-student-2/512/HumanV1-15-512.png',
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
    features: IFeature[];
    directionsGetResponse: IDirectionsGetResponse | undefined;
    enableClickMap: boolean;
    ourPickupPoints: IPickupPoint[];
}

interface MyComponentProps {
    geoData: Coords;
    setGeoData: React.Dispatch<React.SetStateAction<Coords>>;
}

const MapClick: React.FC<MyComponentProps> = ({ geoData, setGeoData }) => {
    useMapEvents({
        click: (e) => {
            console.log('location clicked:', e.latlng);
            setGeoData(e.latlng);
        },
    });

    return null;
}

function MapZoom() {
    const map = useMapEvents({
        zoomend: () => {
            const zoom = map.getZoom();
            map.flyTo(map.getCenter(), zoom);
        },
    });

    return null;
}

export default function MapClient({
    features,
    directionsGetResponse,
    enableClickMap,
    ourPickupPoints
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

            {enableClickMap && <MapClick geoData={geoData} setGeoData={setGeoData} />}

            {ourPickupPoints?.map((pickupPoint, index) => (
                <Marker
                    key={index}
                    position={{ lat: pickupPoint.latitude, lng: pickupPoint.longitude }}
                    icon={wavingPeopleIcon}
                >
                    <Tooltip>
                        {pickupPoint.address}
                    </Tooltip>
                </Marker>
            ))}

            {/* {directionsGetResponse?.routes.map((route, routeIndex) => {
                // const decodedPolyline = polyline.decode(route.geometry).map((coordinate: number[]) => [coordinate[0], coordinate[1]]); // Swap latitude and longitude
                const decodedPolyline = polyline.decode(route.geometry).map((coordinate: number[]) => ({ lat: coordinate[0], lng: coordinate[1] }));

                return (
                    <Polyline
                        key={`route-${routeIndex}`}
                        positions={decodedPolyline}
                    />
                );
            })} */}

            <MapZoom />
        </MapContainer>
    );
}