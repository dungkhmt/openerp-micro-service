import { request } from "api";
import { menuIconMap } from "config/menuconfig";
import { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import ChoseTruckAndOrders from "../ChoseTruckAndOrders";
import OrderArrangement from "../OrderArrangement";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import TruckAndOrder from "./TruckAndOrder";
import MapComponent from "../routing/Map";

const { Box, Typography, Button, Divider, Icon } = require("@mui/material")



const TripDetail = () => {
    const history = useHistory();
    const { tripId } = useParams();
    const [trip, setTrip] = useState([]);
    const [trucks, setTrucks] = useState([]);
    const [truckSelect, setTruckSelect] = useState([]);
    const [orders, setOrders] = useState([]);
    const [ordersSelect, setOrdersSelect] = useState([]);
    const [tripItems, setTripItem] = useState([]);
    useEffect(() => {
        request(
            "post",
            `/truck/`, {}, {}, {}, {},
        ).then((res) => {
            // let truckTmp = res.data.filter(item => checkScheduler(item.id, "truck"))
            console.log("truck", res.data)
            setTrucks(res.data);
            // setTruckSelect(res.data[0])
        });
        request(
            "post",
            `/order/`, {}, {}, {}, {},
        ).then((res) => {
            // let orderTmp = res.data.data.filter(item => checkScheduler(item.id, "order"))
            setOrders(res.data.data);
        });

        request(
            "post",
            `/tripItem/${tripId}`, {}, {}, {}, {},
        ).then((res) => {

            setTripItem(res.data.data);
        });
    }, [])
    useEffect(() => {
        request(
            "post",
            `/trip/${tripId}`, {}, {}, {}, {},
        ).then((res) => {
            console.log("res", res)
            setOrdersSelect(res.data.data.orders);
            trucks.forEach((item) => {
                if (item.id == res.data.data.truckId) {
                    setTruckSelect(item)
                }

            })
        });
    }, [trucks, tripId])

    return (
        <Box className="trip-detail">
            <Box className="header-trip-detail">
                <Box className="headerScreen-trip-detail-go-back"
                    onClick={() => history.goBack()}
                    sx={{ cursor: "pointer" }}
                >
                    <Icon>
                        {menuIconMap.get("ArrowBackIosIcon")}
                    </Icon>
                    <Typography>Go back shipment Screen</Typography>
                </Box>
                <Box className="header-trip-detail-text">
                    Trip Detail
                </Box>

                {/* <Box className="header-trip-detail-btn">
                    <Button variant="outlined" color="error" className="header-trip-detail-btn-cancel"
                        onClick={handleCancelCreateTrip}
                    >Cancel</Button>
                    <Button variant="contained" className="header-trip-detail-btn-save"
                    onClick={handleSubmit}
                    >Save</Button>
                </Box> */}
            </Box>
            <Divider className="divider-trip-detail" />
            <Box className="content-trip">
                <Box className="content-truck-and-orders">
                    <TruckAndOrder trucks={trucks} setTruckSelect={setTruckSelect} truckSelect={truckSelect}
                        orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} tripId={tripId} />
                </Box>
                <Box className="order-arrangement">
                    <OrderArrangement ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} tripId={tripId} />
                </Box>
                <Box className="map-order">
                    <Box>
                        <MapComponent tripItems={tripItems} />
                    </Box>
                    <Box>Thong tin trip</Box>
                </Box>
            </Box>
        </Box>
    )
}
export default TripDetail;