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
import { deleteTrip, getTripByTripId, updateTrip } from "api/TripAPI";
import TruckAndOrdersInTrip from "../tripComponent/TruckAndOrdersInTrip";
import OrderArrangementInTrip from "../tripComponent/OrderArrangementInTrip";

const { Box, Typography, Button, Divider, Icon, Alert } = require("@mui/material")



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

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        getTrucks({}).then((res) => {
            setTrucks(res?.data.truckModels);
        });
        getOrders({}).then((res) => {
            setOrders(res?.data.data.orderModels);
        });
        getTripItemByTripId(tripId).then((res) => {
            console.log("tripItem1111111", res?.data.data.sort((a, b) => a.id - b.id))
            setTripItem(res?.data.data.sort((a, b) => a.id - b.id));
        });
        getTripByTripId(tripId).then((res) => {
            setTrip(res?.data.data);
        });
    }, [loading])
    useEffect(() => {
        getTripByTripId(tripId).then((res) => {
            console.log("res", res)
            setOrdersSelect(res.data.data.orders);
            trucks.forEach((item) => {
                if (item.id === res?.data.data.truckId) {
                    setTruckSelect(item)
                }
            })
        });
    }, [trucks])
    const handleCancelTrip = () => {
        let data = [];
        data.push(tripId);
        deleteTrip({ listUidTrip: data }).then((res) => {
            console.log(res);
            setToastMsg("Delete Trip Success");
            setToastType("success");
            setToast(true);
            setTimeout(() => {
                setToast(false);
            }, "3000");
            // setFlag(!flag);
            history.push(`/shipment/detail/${shipmentId}`);
        })
    }
    const handleSubmit = () => {
        let tripItemTmp = [];
        tripItems.forEach((item, index) => {
            let tripItem = {
                seq: index + 1,
                action: item.action,
                facilityId: item.facilityId,
                orderCode: item.orderCode,
                arrivalTime: item.arrivalTime,
                departureTime: item.departureTime,
                containerId: item?.containerId,
                trailerId: item?.trailerId,
                type: item?.type
            }
            tripItemTmp.push(tripItem);
        })
        let orderSubmit = [];
        ordersSelect.forEach((item) => {
            orderSubmit.push(item.id);
        })
        let dataSubmit = {
            truckId: truckSelect?.id,
            orderIds: orderSubmit,
            tripItemModelList: tripItemTmp
        }
        updateTrip(tripId, dataSubmit).then((res) => {
            setToastType("success");
            setToast(true);
            setToastMsg("Update Trip Success !!!")
            setTimeout(() => {
                setToast(false);
            }, "3000");
            setFlag(false);
            setLoading(!loading);
        })
    }
    console.log("tripItems113", tripItems)
    return (
        <Box className="trip-detail">
            <Box className="toast">
                {toastOpen ? (
                    <Alert variant="filled" severity={toastType} >
                        <strong>{toastMsg}</strong >
                    </Alert >) : null}
            </Box>
            <Box className="headerScreen-trip-detail">
                <Box className="headerScreen-trip-detail-go-back"
                    onClick={() => history.push(`/shipment/detail/${shipmentId}`)}
                    sx={{ cursor: "pointer" }}
                >
                    <Icon>
                        {menuIconMap.get("ArrowBackIosIcon")}
                    </Icon>
                    <Typography>Go back shipment detail</Typography>
                </Box>
                <Box className="headerScreen-trip-detail-info">
                    <Box className="title-header">
                        <Typography >Trip detail</Typography>
                    </Box>
                    <Box className="btn-header">
                        {trip?.status === "SCHEDULED" ? (
                        <Button variant="outlined" color="error" className="header-trip-detail-btn-cancel"
                            sx={{marginRight: '32px'}}
                            onClick={handleCancelTrip}
                        >Delete</Button>) : null }
                        <Button variant="contained" className="header-trip-detail-btn-save"
                            onClick={handleSubmit}
                        >Save</Button>
                    </Box>
                </Box>
            </Box>
            <Divider className="divider-trip-detail" />
            <Box className="content-trip-v2">
                {/* <Box className="content-truck-and-orders">
                    <TruckAndOrder trucks={trucks} setTruckSelect={setTruckSelect} truckSelect={truckSelect}
                        orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} tripId={tripId} setFlag={setFlag}/>
                </Box> */}
                <Box className="content-truck-and-orders-v2">
                    <TruckAndOrdersInTrip trucks={trucks} setTruckSelect={setTruckSelect} truckSelect={truckSelect}
                        orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} tripId={tripId} setFlag={setFlag} />
                </Box>
                {/* <Box className="order-arrangement">
                    <OrderArrangement ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} tripItems={tripItems} flag={flag}/>
                </Box> */}
                <Box className="order-arrangement-v2">
                    <OrderArrangementInTrip ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} tripItems={tripItems} flag={flag} />
                </Box>
                <Box className="map-order-v2">
                    <Box>
                        <MapComponent tripItems={tripItems} />
                    </Box>
                    {/* <Box>Thong tin trip</Box> */}
                </Box>
            </Box>
        </Box>
    )
}
export default TripDetail;