import React from 'react'
import {MapContainer, Marker, Popup, TileLayer} from 'react-leaflet'
import GeoCoderMarker from '../GeoCoderMarker/GeoCoderMarker'
import L from "leaflet";
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
const DefaulIcon = L.icon ({
    iconUrl : icon,
    iconSize: [10, 10], // Kích thước của biểu tượng
    // iconAnchor: [16, 32],
    shadowUrl: iconShadow
})

const Map = ({address, district, province, position, setPosition}) => {
    // console.log("position", position)
    return (
        <MapContainer
            center={position.length > 0 ? position : [21.0, 105]}
            zoom={7}
            scrollWheelZoom={false}
            style={{
                height: "350px",
                width: "100%",
                marginTop: "20px",
                zIndex: 0,

            }}
        >
            <TileLayer url='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'/>
            {
                (district !== null && province !== null && address !== null) && (
                    <GeoCoderMarker address={`${address} ${district} ${province} Việt Nam`} setPosition={setPosition}/>)
            }
        </MapContainer>
    )
}

export default Map