import { Box, Fab, Typography } from "@mui/material";
import React, { useContext, useEffect, useRef, useState } from "react";
import { MapContainer, Marker, Popup, TileLayer, Tooltip } from "react-leaflet";
import RoutingMachine from "./RoutingMachine";
import L from "leaflet";
import '../styles.scss';

const MapComponent = ({ tripItems }) => {
    // create a ref
    const rMachine = useRef();
    const [map, setMap] = useState(null);
    const [point, setPoint] = useState([]);
    const [makers, setMaker] = useState([]);
    const [pointsMap, setPointsMap] = useState(new Map());
    // let pointsMap = new Map();
    useEffect(() => {
        let pointTmp = [];
        console.log("tripItems===map", tripItems);
        if (!pointsMap) {
        } else {
            setPointsMap(pointsMap.clear())
        }
        let pointMapTmp = new Map();
        tripItems?.forEach(element => {
            let pointLoop = L.latLng(element.latitude, element.longitude);
            pointTmp.push(pointLoop);

            if (!pointMapTmp || !pointMapTmp?.get(element.facilityId)) {
                console.log("11");
                pointMapTmp?.set(element.facilityId, [element]);
            }
            else {
                let pointInMap = pointMapTmp?.get(element.facilityId);
                pointInMap.push(element);
                console.log("22")
                pointMapTmp?.set(element.facilityId, pointInMap);
            }
        });
        setPointsMap(pointMapTmp);
        setPoint(pointTmp);
        rMachine.current?.setWaypoints(pointTmp);

    }, [tripItems, rMachine])

    console.log("pointMap", pointsMap)
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
                {/* {pointsMap?.forEach((value, key, map) => {
                    console.log("value", value);
                    if (value.length > 1) {
                        return (
                            <MarkerClusterGroup
                                iconCreateFunction={createClusterCustomIcon}
                                spiderfyOnMaxZoom={true}
                                polygonOptions={{
                                    fillColor: '#ffffff',
                                    color: '#f00800',
                                    weight: 5,
                                    opacity: 1,
                                    fillOpacity: 0.8,
                                  }}
                                  showCoverageOnHover={true}
                            >
                                {value.map((item) => {
                                    return (
                                        <Marker position={L.latLng(item.latitude, item.longitude)}>
                                            <Popup>
                                                {item.orderCode}
                                            </Popup>
                                            <Tooltip>Tooltip for Marker</Tooltip>
                                        </Marker>
                                    )
                                })}
                            </MarkerClusterGroup>
                        );
                    }
                })} */}

                <RoutingMachine ref={rMachine} waypoints={point} />
                {tripItems?.map((item) => {
                    let listPointSame = pointsMap?.get(item.facilityId);
                    if (listPointSame?.length > 1) {
                        return (
                            <Marker position={L.latLng(item.latitude, item.longitude)}
                                icon={new L.DivIcon({
                                    html: `<Box size="small" class="number" variant="extended">Group</Box>`,
                                    iconSize: L.point(33, 33, true),
                                })}
                            >
                                <Popup>
                                    <Box sx={{ height: '400px' }}>
                                        {listPointSame?.map(pointTrip => {
                                            return (
                                                <Box sx={{ marginBottom: '16px' }}>
                                                    <Typography sx={{ margin: '0px' }}>Seq: {pointTrip.seq}</Typography>
                                                    <Typography>Facility: {pointTrip.facilityCode}</Typography>
                                                    <Typography>Entity: {pointTrip.orderCode}</Typography>
                                                    <Typography>Action: {pointTrip.action}</Typography>
                                                </Box>
                                            )
                                        })}
                                    </Box>
                                </Popup>
                                <Tooltip>Group Action In Point</Tooltip>
                            </Marker>
                        )
                    }
                    else {
                        return (
                            <Marker position={L.latLng(item.latitude, item.longitude)}
                                icon={new L.DivIcon({
                                    html: `<Box size="small" class="number" variant="extended">${item.seq}</Box>`,
                                    iconSize: L.point(33, 33, true),
                                })}
                            >
                                <Popup>
                                    <Box>
                                        <Typography sx={{ margin: '0px' }}>Seq: {item.seq}</Typography>
                                        <Typography>Facility: {item.facilityCode}</Typography>
                                        <Typography>Entity: {item.orderCode}</Typography>
                                        <Typography>Action: {item.action}</Typography>
                                    </Box>
                                </Popup>
                                <Tooltip>At Facility {item.facilityCode}</Tooltip>
                            </Marker>
                        )
                    }
                })}

            </MapContainer>
        </Box>
    )
}
export default MapComponent;