import { Box } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import './styles.scss';
import ChoseTruckAndOrders from "./ChoseTruckAndOrders";
import OrderArrangement from "./OrderArrangement";

const TripDetail = () => {
    const [trucks, setTrucks] = useState([]);
    const [truckId, setTruckId] = useState();

    useEffect(() => {
        request(
            "post",
            `/truck/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("truck==========", res.data)
            setTrucks(res.data);
        });
    }, [])
    return (
        <Box className="content-trip">
            <Box className="content-truck-and-orders">
                <ChoseTruckAndOrders trucks={trucks} setTruckId={setTruckId} />
            </Box>
            <Box className="order-arrangement">
                <OrderArrangement />
            </Box>
            <Box className="map-order">
                <Box>
                    <MapContainer center={[51.505, -0.09]} zoom={13} scrollWheelZoom={false} style={{ height: "70vh" }}>
                        <TileLayer
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />
                        <Marker position={[51.505, -0.09]}>
                            <Popup>
                                A pretty CSS3 popup. <br /> Easily customizable.
                            </Popup>
                        </Marker>
                        <Marker position={[51.605, -0.09]}>
                            <Popup>
                                A pretty CSS3 popup. <br /> Easily customizable.
                            </Popup>
                        </Marker>
                    </MapContainer>
                </Box>
                <Box>Thong tin trip</Box>
            </Box>
        </Box>

    )
}
export default TripDetail;