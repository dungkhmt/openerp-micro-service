import React, { useEffect, useState, useRef } from 'react';
import { MapContainer, Marker, Popup, TileLayer, useMapEvents } from 'react-leaflet'
import { Box, Button, Container } from '@mui/material';
import L from 'leaflet';


import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import HomeIcon from '@mui/icons-material/Home';
import CardHub from '../CardHub';
import HubPopup from '../HubPopup';
const position = [21.0146843, 105.7660694]
const myIcon = L.icon({
    iconUrl: require('../../../../assets/icons/hub.png'),
    iconSize: [30, 42],
    iconAnchor: [15, 42],
    popupAnchor: [0, -42],
});

function MapComponent({ hubs, mapRef, isEdit, setCoordinates, onHubEdit, onHubDelete }) {




    return (
        <Box
            sx={{
                position: "relative",
                height: "100%",
                width: "100%",

            }}
        >
            <MapContainer ref={mapRef} center={position} zoom={13}>

                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {!isEdit && hubs.map((hub, index) => (
                    <Marker key={index} position={[hub.latitude, hub.longitude]}
                        icon={myIcon}
                    >
                        <Popup>
                            <HubPopup hub={hub} onHubDelete={onHubDelete} onHubEdit={onHubEdit} />
                        </Popup>
                    </Marker>
                ))}
                {isEdit && <LocationMarker setCoordinates={setCoordinates} />}
            </MapContainer>
        </Box>



    );
}



export default MapComponent;

function LocationMarker({ setCoordinates }) {
    const [position, setPosition] = useState(null)
    const map = useMapEvents({
        click(e) {
            var coord = e.latlng;
            var lat = coord.lat;
            var lng = coord.lng;
            setPosition(e.latlng)
            setCoordinates(lat, lng)
        },

    })

    return position === null ? null : (
        <Marker position={position}
            icon={myIcon}
        >
            <Popup>New Hub</Popup>
        </Marker>
    )
}
