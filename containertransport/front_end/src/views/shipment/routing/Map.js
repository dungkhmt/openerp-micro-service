import { Box } from "@mui/material";
import React, { useContext, useEffect, useRef, useState } from "react";
import { MapContainer, Marker, Popup, TileLayer, Tooltip } from "react-leaflet";
import RoutingMachine from "./RoutingMachine";
import L, { MarkerCluster } from "leaflet";
import MarkerClusterGroup from "react-leaflet-markercluster";


const MapComponent = ({ tripItems }) => {
    // create a ref
    const rMachine = useRef();
    const [map, setMap] = useState(null);
    const [point, setPoint] = useState([]);
    const [makers, setMaker] = useState([]);
    const pointMap = new Map();
    useEffect(() => {
        let pointTmp = [];
        console.log("tripItems===map", tripItems)
        tripItems.forEach(element => {
            let pointLoop = L.latLng(element.latitude, element.longitude);
            pointTmp.push(pointLoop);
            // console.log("pointMap", pointMap.get(element.facilityId))
            // if (!pointMap.get(element.facilityId)) {
            //     pointMap.set(element.facilityId, [element]);
                
            // }
            // else {
            //     let pointInMap = pointMap.get(element.facilityId);
            //     pointInMap.push(element);
            //     pointMap.set(element.facilityId, pointInMap)
            // }
        });
        setPoint(pointTmp);
        rMachine.current?.setWaypoints(pointTmp);

    }, [tripItems, rMachine])

    const createClusterCustomIcon = function (cluster) {
        return L.divIcon({
            html: `<span>${cluster.getChildCount()}</span>`,
            className: 'custom-marker-cluster',
            iconSize: L.point(33, 33, true),
        })
    }
    
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
                {/* {pointMap.forEach((value, key, map) => {
                    console.log("value", value)
                    if (value.length > 1) {
                        return (
                            <MarkerClusterGroup
                                iconCreateFunction={createClusterCustomIcon}
                            >
                                {value.map((item) => {
                                    return (
                                        <Marker position={L.latLng(item.latitude, item.longitude)}>
                                            <Popup>
                                                A pretty CSS3 popup. <br /> Easily customizable.
                                            </Popup>
                                            <Tooltip>Tooltip for Marker</Tooltip>
                                        </Marker>
                                    )
                                })}
                            </MarkerClusterGroup>
                        );
                    } else {
                        return (
                            <Marker position={L.latLng(value.latitude, value.longitude)}>
                                <Popup>
                                    A pretty CSS3 popup. <br /> Easily customizable.
                                </Popup>
                                <Tooltip>Tooltip for Marker</Tooltip>
                            </Marker>
                        )
                    }
                })} */}
                <RoutingMachine ref={rMachine} waypoints={point} />
                {/* {point.map((item) => {
                    return(
                        <Marker position={item}>
                                <Popup>
                                    A pretty CSS3 popup. <br /> Easily customizable.
                                </Popup>
                                <Tooltip>Tooltip for Marker</Tooltip>
                            </Marker>
                    )
                })} */}
                
            </MapContainer>
        </Box>
    )
}
export default MapComponent;