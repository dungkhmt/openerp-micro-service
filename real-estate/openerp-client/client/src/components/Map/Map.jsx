import React from 'react'
import {MapContainer, TileLayer} from 'react-leaflet'
import GeoCoderMarker from '../GeoCoderMarker/GeoCoderMarker'
const Map = ({address, district, province}) => {
    // console.log(address, district, province)
    return (
        <MapContainer
            center={[21.0,105]}
            zoom={4}
            scrollWheelZoom={false}
            style={{
                height: "40vh",
                width: "100%",
                marginTop: "20px",
                zIndex: 0,

            }}
        >
            <TileLayer url='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'/>
            {
                (district !== undefined && province !== undefined && address !== null) && (<GeoCoderMarker address={`${address} ${district} ${province} Việt Nam`} />)
            }
        </MapContainer>
    )
}

export default Map