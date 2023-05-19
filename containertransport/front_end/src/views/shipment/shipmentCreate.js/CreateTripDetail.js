import { Box, Button, Divider, Typography } from "@mui/material";
import { request } from "api";
import React, { useContext, useEffect, useState } from "react";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import '../styles.scss';
import ChoseTruckAndOrders from "../ChoseTruckAndOrders";
import OrderArrangement from "../OrderArrangement";
import { useHistory, useLocation } from "react-router-dom";
import { MyContext } from "contextAPI/MyContext";
import { Map } from "leaflet";
import RoutingMachine from "../routing/RoutingMachine";
import MapComponent from "../routing/Map";

const CreateTripDetail = () => {
    const {truckScheduler, setTruckScheduler, tripsCreate, setTripCreate} = useContext(MyContext);
    const {ordersScheduler, setOrderScheduler, preferred_username} = useContext(MyContext);
    const [truck, setTruck] = useState();
    const [trucks, setTrucks] = useState([]);
    const [orders, setOrders] = useState([]);
    const [ordersSelect, setOrdersSelect] = useState([]);
    const [truckSelect, setTruckSelect] = useState();
    const [map , setMap] = useState();
    const [test, setTest] = useState("anhvu");
    
    const [tripItems, setTripItem] = useState([]);
    const history = useHistory();
    const location = useLocation();
    const tripsTmpId = useState(location?.search);

    const checkScheduler = (id, type) => {
        let check = true;
        if(type === "truck"){
            truckScheduler.forEach((item) => {
                if(item.id === id) 
                    return check = false;
            });
        } else {
            ordersScheduler.forEach((item) => {
                if(item === id) 
                    return check = false;
            });
        }
        return check;
    }
    const updateScheduler = (orderSubmir) => {
        let truckSchedulerUpdate = truckScheduler
        truckSchedulerUpdate.push(truck);
        setTruckScheduler(truckSchedulerUpdate);
        let ordersSchedulerUpdate = ordersScheduler;
        setOrderScheduler(ordersSchedulerUpdate.concat(orderSubmir));
    }

    useEffect(() => {
        request(
            "post",
            `/truck/`, {}, {}, {}, {},
        ).then((res) => {
            let truckTmp = res.data.filter(item => checkScheduler(item.id, "truck"))
            // console.log("truck", res.data)
            setTrucks(truckTmp);
        });
        request(
            "post",
            `/order/`, {}, {}, {}, {},
        ).then((res) => {
            let orderTmp = res.data.data.filter(item => checkScheduler(item.id, "order"))
            setOrders(orderTmp);
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
        history.goBack();
    }
    const handleSubmit = () => {
        let orderSubmir = [];
        let tripItemTmp = [];
        ordersSelect.forEach((item) => {
            orderSubmir.push(item.id);
        })
        tripItems.forEach((item, index) => {
            let tripItem = {
                seqInTrip: index+1,
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
        let trips = tripsCreate;
        let data = {
            id: tripsCreate?.length > 0 ? tripsCreate.length + 1 : 0,
            truck: truck,
            created_by_user_id: preferred_username,
            orderIds: orderSubmir,
            tripItemModelList: tripItemTmp
        }
        trips.push(data)
        setTripCreate(trips);
        updateScheduler(orderSubmir);
        history.goBack();
        console.log("data", data);
    }
    console.log("tripItems", tripItems);
    useEffect(() => {

    }, [tripItems])
    return (
        <Box className="trip-create">
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
                    <ChoseTruckAndOrders trucks={trucks} setTruck={setTruck} truckSelected={truckSelect} setTruckSelect={setTruckSelect}
                    orders={orders} ordersSelect={ordersSelect} setOrdersSelect={setOrdersSelect} />
                </Box>
                <Box className="order-arrangement">
                    <OrderArrangement ordersSelect={ordersSelect} setTripItem={setTripItem} truckSelected={truckSelect} />
                </Box>
                <Box className="map-order">
                    <Box>
                        <MapComponent tripItems={tripItems}/>
                    </Box>
                    <Box>Thong tin trip</Box>
                </Box>
            </Box>
        </Box>
    )
}
export default CreateTripDetail;