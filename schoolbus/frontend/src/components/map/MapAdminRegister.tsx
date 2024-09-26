import React, { useState, useEffect, FC, RefObject, useRef } from 'react';
import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, useMap, useMapEvents, GeoJSON, Polyline, Tooltip } from 'react-leaflet';
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
    setSelectedAutoCompleteData: React.Dispatch<React.SetStateAction<IFeature | null>>;
    pickupPoints: IPickupPointTable[];
    directionsGetResponse: IDirectionsGetResponse | undefined;
    enableClickMap: boolean;
    selectedPendingRequestRegistration: IRequestRegistrationResponse[] | null;
    pickupPointId: number | null;
    setPickupPointId: React.Dispatch<React.SetStateAction<number | null>>;
}

interface MyComponentProps {
    geoData: Coords;
    setGeoData: React.Dispatch<React.SetStateAction<Coords>>;
    setSelectedAutoCompleteData: React.Dispatch<React.SetStateAction<IFeature | null>>;
}

const MapClick: React.FC<MyComponentProps> = ({ geoData, setGeoData, setSelectedAutoCompleteData }) => {
    useMapEvents({
        click: (e) => {
            console.log('location clicked:', e.latlng);
            setGeoData(e.latlng);
            // code here
            setSelectedAutoCompleteData(prevData => {
                if (prevData === null) {
                    // return an object with geometry and properties
                    let result: IFeature = {
                        type: 'Feature',
                        geometry: {
                            type: 'Point',
                            coordinates: [e.latlng.lng, e.latlng.lat]
                        },
                        properties: {
                            name: 'Điểm tự chấm',
                            street: 'Điểm tự chấm',
                            county: 'Điểm tự chấm',
                            region: 'Điểm tự chấm',
                            country: 'Điểm tự chấm',
                            continent: 'Điểm tự chấm',
                            id: 'Điểm tự chấm',
                            label: 'Điểm tự chấm'
                        }
                    };

                    return result;
                }

                return {
                    ...prevData,
                    geometry: {
                        ...prevData.geometry,
                        coordinates: [e.latlng.lng, e.latlng.lat]
                    },
                    properties: prevData.properties || {}
                };
            });
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

export default function MapAdminRegister(
    {
        features,
        setSelectedAutoCompleteData,
        pickupPoints,
        directionsGetResponse,
        enableClickMap,
        selectedPendingRequestRegistration,
        pickupPointId,
        setPickupPointId,
    }: MapProps) {
    const zoomRef = useRef<number>(12);
    const [geoData, setGeoData] = useState<Coords>({ lat: 21.028511, lng: 105.804817 });

    const center: Coords = { lat: geoData.lat, lng: geoData.lng };

    return (
        <MapContainer
            center={center}
            zoom={12}
            style={{ height: '85vh' }}
        >
            <TileLayer
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            {features.map((feature, index) => (
                <Marker
                    key={index}
                    position={{ lat: feature.geometry.coordinates[1], lng: feature.geometry.coordinates[0] }}
                    icon={locationIcon}
                />
            ))}

            <ChangeView coords={center} />

            {/* enable click map */}
            {enableClickMap && <MapClick geoData={geoData} setGeoData={setGeoData} setSelectedAutoCompleteData={setSelectedAutoCompleteData} />}

            {/* pickup points */}
            {pickupPoints?.map((pickupPointTable, index) => (
                <Marker
                    key={index}
                    position={{ lat: pickupPointTable.pickupPoint.latitude, lng: pickupPointTable.pickupPoint.longitude }} //todo: add detail onclick later when having enough data from client flow
                    icon={
                        pickupPointTable.pickupPoint.address === 'SCHOOL' ? schoolIcon
                            : pickupPointId === pickupPointTable.pickupPoint.id ? manipulateLocationIcon
                                : locationIcon
                    }
                    eventHandlers={{
                        click: () => {
                            if (!enableClickMap) {
                                setPickupPointId(prevId => prevId === pickupPointTable.pickupPoint.id ? null : pickupPointTable.pickupPoint.id);
                            }
                        }
                    }}
                >
                    <Tooltip>
                        {pickupPointTable.pickupPoint.address}
                    </Tooltip>
                </Marker>
            ))}

            {/* selected pending request registration */}
            {selectedPendingRequestRegistration?.map((requestRegistrationResponse, index) => {
                // Generate a small random offset (for duplicate markers)
                const latOffset = Math.random() * 0.0001 - 0.00005;
                const lngOffset = Math.random() * 0.0001 - 0.00005;

                return (
                    <Marker
                        key={index}
                        position={{
                            lat: requestRegistrationResponse.requestRegistration.latitude + latOffset,
                            lng: requestRegistrationResponse.requestRegistration.longitude + lngOffset
                        }}
                        icon={wavingPeopleIcon}
                    >
                        {requestRegistrationResponse.requestRegistration.address}
                    </Marker>
                );
            })}

            {/* directions */}
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