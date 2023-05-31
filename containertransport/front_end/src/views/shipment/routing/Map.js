import { Box } from "@mui/material";
import React, { useContext, useEffect, useRef, useState } from "react";
import { MapContainer, TileLayer } from "react-leaflet";
import RoutingMachine from "./RoutingMachine";
import L from "leaflet";


const MapComponent = ({tripItems}) => {
    // create a ref
    const rMachine = useRef();
    const [map, setMap] = useState(null);
    const [point, setPoint] = useState([]);
    useEffect(() => {
        let pointTmp = [];
        console.log("tripItems===map", tripItems)
        tripItems.forEach(element => {
            pointTmp.push(L.latLng(element.latitude, element.longitude))
        });
        setPoint(pointTmp);
        rMachine.current?.setWaypoints(pointTmp);
        
    }, [tripItems, rMachine])
    console.log("point", point)
    return (
        <Box>
            <MapContainer 
            center={[21.018172, 105.829754]} 
            zoom={13}
            scrollWheelZoom={true} style={{ height: "70vh" }}
            whenCreated={map => setMap(map)}
            >
                <TileLayer
                // url="https://{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}"
                // attribution="Map data © Google"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <RoutingMachine ref={rMachine} waypoints={point} />
            </MapContainer>
        </Box>
    )
}
export default MapComponent;