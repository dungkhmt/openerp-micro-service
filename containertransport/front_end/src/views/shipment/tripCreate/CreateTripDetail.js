import { Alert, Box, Button, Divider, Typography } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import '../styles.scss';
import OrderArrangement from "../tripComponent/OrderArrangement";
import { useHistory, useLocation, useParams } from "react-router-dom";
import { MyContext } from "contextAPI/MyContext";
import { Map } from "leaflet";
import RoutingMachine from "../routing/RoutingMachine";
import MapComponent from "../routing/Map";
import TruckAndOrder from "../tripComponent/TruckAndOrder";
import { createTrip } from "api/TripAPI";
import { getOrders } from "api/OrderAPI";
import { getTrucks } from "api/TruckAPI";

const CreateTripDetail = () => {
    const { truckScheduler, setTruckScheduler, tripsCreate, setTripCreate } = useContext(MyContext);
    const { ordersScheduler, setOrderScheduler, preferred_username } = useContext(MyContext);
    const { shipmentId } = useParams();
    const [truck, setTruck] = useState();
    const [trucks, setTrucks] = useState([]);
    const [orders, setOrders] = useState([]);
    const [ordersSelect, setOrdersSelect] = useState([]);
    const [truckSelect, setTruckSelect] = useState();
    const [flag, setFlag] = useState(false);

    const [tripItems, setTripItem] = useState([]);
    const history = useHistory();
    const location = useLocation();
    const tripsTmpId = useState(location?.search);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();

    const checkScheduler = (id, type) => {
        let check = true;
        if (type === "truck") {
            truckScheduler.forEach((item) => {
                if (item.id === id)
                    return check = false;
            });
        } else {
            ordersScheduler.forEach((item) => {
                if (item === id)
                    return check = false;
            });
        }
        return check;
    }

    useEffect(() => {
        getTrucks({}).then((res) => {
            // let truckTmp = res.data.filter(item => checkScheduler(item.id, "truck"))
            // console.log("truck", res.data)
            setTrucks(res.data.truckModels);
        });
        getOrders({}).then((res) => {
            // let orderTmp = res.data.data.filter(item => checkScheduler(item.id, "order"))
            setOrders(res.data.data.orderModels);
        });
        // if (tripsTmpId[0] !== null) {
        //     console.log("tripTmp",tripsCreate[tripsTmpId[0].slice(1)]);
        //     trucks.forEach((item) => {
        //         if(item.id == tripsCreate[tripsTmpId[0].slice(1)].truckId) {
        //             setTruckSelect(item);
        //         }
        //     })
        //     // (tripsTmpId[0].slice(1));

        // }
    }, [])
    const handleCancelCreateTrip = () => {
        history.push(`/shipment/detail/${shipmentId}`);
    }
    const handleSubmit = () => {
        let orderSubmir = [];
        let tripItemTmp = [];
        ordersSelect.forEach((item) => {
            orderSubmir.push(item.id);
        })
        tripItems.forEach((item, index) => {
            let tripItem = {
                seqInTrip: index + 1,
                action: item.action,
                facilityId: item.facilityId,
                orderId: item.orderCode,
                arrivalTime: item.arrivalTime,
                departureTime: item.departureTime,
                containerId: item?.container?.id,
                trailerId: item?.trailerId
                // time
            }
            tripItemTmp.push(tripItem);
        })
        let dataSubmit = {
            shipmentId: shipmentId,
            createBy: preferred_username,
            tripContents: {
                truckId: truckSelect?.id,
                orderIds: orderSubmir,
                tripItemModelList: tripItemTmp
            }
        }
        console.log("data", dataSubmit);
        createTrip(dataSubmit).then((res) => {
            setToastType("success");
            setToast(true);
            setTimeout(() => {
                setToast(false);
                history.push({
                    pathname:`/shipment/detail/${shipmentId}`,
                })
            }, "1000");
        })
    }
    console.log("truckSelect", truckSelect);
    useEffect(() => {

    }, [tripItems])
    return (
        <Box className="trip-create">
            <Box className="toast">
                {toastOpen ? (
                    toastType === "success" ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong> Created Trip Success !!!</strong >
                        </Alert >
                    ) : (
                        <Alert variant="filled" severity={toastType} >
                            <strong> Created Trip False !!!</strong >
                        </Alert >
                    )) : null
                }
            </Box>
            <Box className="header-trip-detail">
                <Typography className="header-trip-detail-text">Create Trip</Typography>
                <Box className="header-trip-detail-btn">
                    <Button variant="outlined" color="error" className="header-trip-detail-btn-cancel"
                        onClick={handleCancelCreateTrip}
                    >Cancel</Button>
                    <Button variant="contained" className="header-trip-detail-btn-save"
                        onClick={handleSubmit}
                    >Save</Button>
                </Box>
            </Box>
            <Divider className="divider-trip-detail" />
            <Box className="content-trip">
                <Box className="content-truck-and-orders">
                    <TruckAndOrder trucks={trucks} setTruck={setTruck} truckSelected={truckSelect} setTruckSelect={setTruckSelect}
                        orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} setFlag={setFlag}/>
                </Box>
                <Box className="order-arrangement">
                    <OrderArrangement ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} flag={flag} />
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
export default CreateTripDetail;