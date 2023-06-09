import { menuIconMap } from "config/menuconfig";
import { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import OrderArrangement from "../tripComponent/OrderArrangement";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import MapComponent from "../routing/Map";
import TruckAndOrder from "../tripComponent/TruckAndOrder";
import { getTripItemByTripId } from "api/TripItemAPI";
import { getTrucks } from "api/TruckAPI";
import { getOrders } from "api/OrderAPI";
import { getTripByTripId } from "api/TripAPI";

const { Box, Typography, Button, Divider, Icon } = require("@mui/material")



const TripDetail = () => {
    const history = useHistory();
    const { shipmentId, tripId } = useParams();
    const [trip, setTrip] = useState([]);
    const [trucks, setTrucks] = useState([]);
    const [truckSelect, setTruckSelect] = useState([]);
    const [orders, setOrders] = useState([]);
    const [ordersSelect, setOrdersSelect] = useState([]);
    const [tripItems, setTripItem] = useState([]);
    const [flag, setFlag] = useState(false);
    useEffect(() => {
        getTrucks({}).then((res) => {
            setTrucks(res.data.truckModels);
        });
        getOrders({}).then((res) => {
            setOrders(res.data.data.orderModels);
        });
        getTripItemByTripId(tripId).then((res) => {
            console.log("tripItem1111111", res.data.data.sort((a, b) => a.id - b.id))
            setTripItem(res.data.data.sort((a, b) => a.id - b.id));
        });
    }, [])
    useEffect(() => {
        getTripByTripId(tripId).then((res) => {
            console.log("res", res)
            setOrdersSelect(res.data.data.orders);
            trucks.forEach((item) => {
                if (item.id == res.data.data.truckId) {
                    setTruckSelect(item)
                }
            })
        });
    }, [trucks])
    return (
        <Box className="trip-detail">
            <Box className="header-trip-detail">
                <Box className="headerScreen-trip-detail-go-back"
                    onClick={() => history.push(`/shipment/detail/${shipmentId}`)}
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
                        orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} tripId={tripId} setFlag={setFlag}/>
                </Box>
                <Box className="order-arrangement">
                    <OrderArrangement ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} tripItems={tripItems} flag={flag}/>
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