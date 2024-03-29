import React, { useEffect, useState } from 'react'
import { Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import "leaflet/dist/leaflet.css"
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";
import * as ELG from 'esri-leaflet-geocoder'
import {apiGetPosition} from "../../services/app";

const DefaulIcon = L.icon ({
    iconUrl : icon,
    shadowUrl: iconShadow
})
L.Marker.prototype.options.icon = DefaulIcon


const GeoCoderMarker = ({address}) => {
    const map = useMap()
    const [position, setPosition] = useState([21.0035108,105.8392403])

    useEffect( ()=> {

        console.log(address)
        const fetchData = async () => {
            const response = await apiGetPosition(address);
            if (response.status === 200) {
                const lng = response?.data.features[0].center[0];
                const lat = response?.data.features[0].center[1];
                setPosition([lat, lng]);
                map.flyTo([lat, lng], 12);
            }
        };

        if (address) {
            fetchData();
        }
        // console.log(address)
        // ELG.geocode().text(address).run((err, results, response)=> {
        //     // console.log(response)
        //     if(results?.results?.length > 0){
        //         const {lat, lng} = results?.results[0].latlng
        //         setPosition([lat, lng])
        //         map.flyTo([lat, lng], 6)
        //     }
        // })
    }, [address])

    console.log(position)
  return (
    <Marker position={position} icon={DefaulIcon}>
        <Popup/>
    </Marker>
  )
}

export default GeoCoderMarker