import React, {useEffect, useState} from 'react'
import {Marker, Popup, useMap} from 'react-leaflet'
import L from 'leaflet'
import "leaflet/dist/leaflet.css"
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import {apiGetPosition} from "../../services/AppRequest";

const DefaulIcon = L.icon({
    iconUrl: icon,
    iconSize: [10, 10], // Kích thước của biểu tượng
    // shadowUrl: iconShadow
})
L.Marker.prototype.options.icon = DefaulIcon

const GeoCoderMarker = ({address, position, setPosition}) => {
    const map = useMap()
    const [positionMarker, setPositionMarker] = useState([21, 105]);
    useEffect(() => {
        const fetchData = async () => {
            const response = await apiGetPosition(address);
            if (response.status === 200) {
                const lng = response?.data.features[0].center[0];
                const lat = response?.data.features[0].center[1];
                setPosition([lat, lng]);
                setPositionMarker([lat, lng])
                map.flyTo([lat, lng], 12);
            }
        };

        console.log(address)
        if (address.length > 12) {
            fetchData();
        }
    }, [address])
    return (
        <Marker position={positionMarker} icon={DefaulIcon}>
            <Popup/>
        </Marker>
    )
}

export default GeoCoderMarker