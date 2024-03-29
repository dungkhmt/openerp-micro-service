import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Box, Button, Icon, Typography } from "@mui/material";
import '../styles.scss';
import { DragDropContext } from "react-beautiful-dnd";
import { Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import ModalTripItem from "../tripCreate/ModalTripItem";
import { getTripItemByTripId } from "api/TripItemAPI";
import { menuIconMap } from "config/menuconfig";


const TripItem = ({ index, item, facilities, setFacilities }) => {
    const [open, setOpen] = useState(false);
    const [arrivalTime, setArrivalTime] = useState(item?.arrivalTime);
    const [departureTime, setDepartureTime] = useState(item?.departureTime);

    const handleChangeTime = (time, id, type) => {
        facilities.map((facility, i) => {
            if (facility.code === item.code) {
                if (type == "arrivalTime") {
                    facility.arrivalTime = time;
                } else {
                    facility.departureTime = time;
                }
                console.log("facility", dayjs(new Date(time)));
                return facility;
            } else {
                return facility;
            }
        });
        //   setCounters(nextCounters);
    }
    const handleDeleteTripItem = () => {
        let facilitiesTmp = facilities.filter((tr) => tr?.code != item?.code);
        setFacilities(facilitiesTmp);
    }
    return (
        <Draggable key={item?.code} index={index} draggableId={item?.code}>
            {(provided, snapshot) => (
                <div
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                    style={{
                        // default item style
                        padding: '8px 16px',
                        // default drag style
                        ...provided.draggableProps.style,
                        // customized drag style
                        background: snapshot.isDragging
                            ? 'lightblue'
                            : 'transparent',
                        borderRadius: snapshot.isDragging
                            ? '8px' : '0'
                    }}
                    className="item-facility"

                >
                    <Box className="item-facility-line">
                        <Box onClick={() => setOpen(!open)}>
                            {item?.orderCode} - {item?.facilityName} - {item?.action}
                        </Box>
                        {(item?.type === "Trailer" || (item?.type === "Truck" && item?.action === "STOP")) ? (
                            <Box onClick={handleDeleteTripItem}>
                                <Icon>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                            </Box>) : <Box></Box>}
                    </Box>

                    {open ?
                        (<Box>
                            {item?.type === "Order" ? (
                                <Box className="trips-item-input">
                                    <Box>Container: {item?.containerCode}</Box>
                                </Box>) : null}
                            {item?.type === "Trailer" ? (
                                <Box className="trips-item-input">
                                    <Box>Trailer: {item?.trailerCode}</Box>
                                </Box>) : null}
                            {index != 0 ? (
                                < Box className="trips-item-input">
                                    <Box>Arrival Time:</Box>
                                    <Box>
                                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                                            <DemoContainer components={['DateTimePicker']}>
                                                <DateTimePicker label="Early Delivery Time"
                                                    defaultValue={item?.arrivalTime ? dayjs(new Date(item?.arrivalTime)) : null}
                                                    onChange={(e) => handleChangeTime((new Date(e)).getTime(), item?.code, "arrivalTime")}
                                                />
                                            </DemoContainer>
                                        </LocalizationProvider>
                                    </Box>
                                </Box>) : null
                            }
                            {index != facilities.length - 1 ? (
                                <Box className="trips-item-input">
                                    <Box>Departure Time:</Box>
                                    <Box>
                                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                                            <DemoContainer components={['DateTimePicker']}>
                                                <DateTimePicker label="Early Delivery Time"
                                                    defaultValue={item?.departureTime ? dayjs(new Date(item?.departureTime)) : null}
                                                    onChange={(e) => handleChangeTime((new Date(e)).getTime(), item?.code, "departureTime")}
                                                />
                                            </DemoContainer>
                                        </LocalizationProvider>
                                    </Box>
                                </Box>) : null
                            }
                        </Box>) : null}
                </div>
            )
            }
        </Draggable >
    )
}

const OrderArrangement = ({ ordersSelect, setTripItem, truckSelected, tripItems, flag }) => {
    const [facilities, setFacilities] = useState([]);
    const [facilitiesFinal, setFacilitiesFinal] = useState([]);
    const [open, setOpen] = useState(false);
    const [addTripItem, setAddTripItem] = useState([]);
    const [truckItem, setTruckItem] = useState([]);
    const [trailer, setTrailer] = useState();

    const handleModal = () => {
        setOpen(!open);
    }

    const reorder = (list, startIndex, endIndex) => {
        const result = Array.from(list);
        const [removed] = result.splice(startIndex, 1);
        result.splice(endIndex, 0, removed);

        return result;
    };
    const handleDragEnd = ({ destination, source }) => {
        // reorder list
        if (!destination) return;

        setFacilitiesFinal(reorder(facilitiesFinal, source.index, destination.index));
    };
    useEffect(() => {
        let facilitiesTmp = [];
        if (flag) {
            if (truckSelected) {
                console.log("start");
                console.log("truckSelected", truckSelected)
                let startPoint = {
                    code: truckSelected?.id + "S1",
                    facilityId: truckSelected?.facilityResponsiveDTO?.facilityId,
                    facilityName: truckSelected?.facilityResponsiveDTO?.facilityName,
                    facilityCode: truckSelected?.facilityResponsiveDTO?.facilityCode,
                    action: "DEPART",
                    orderCode: truckSelected?.truckCode,
                    longitude: truckSelected?.facilityResponsiveDTO?.longitude,
                    latitude: truckSelected?.facilityResponsiveDTO?.latitude,
                    arrivalTime: null,
                    container: null,
                    trailerId: null,
                    departureTime: null,
                    type: "truck"
                }
                facilitiesTmp.push(startPoint);
                setTruckItem(facilitiesTmp);
                facilitiesTmp = facilitiesTmp.concat(facilities);
                setFacilitiesFinal(facilitiesTmp);
            }
            if (!truckSelected) {
                facilitiesTmp = facilitiesFinal.filter((faci) => (faci.action != "DEPART"));
                facilitiesTmp = facilitiesTmp.filter((faci) => (faci.action != "STOP"));
                setFacilities(facilitiesTmp);
                setFacilitiesFinal(facilitiesTmp);
                setTruckItem([]);
            }
        }
    }, [truckSelected]);

    useEffect(() => {
        let facilitiesTmp = facilitiesFinal;
        if (flag) {
            console.log("facilitiesBefore111", truckItem);
            if (truckItem.length > 0) {
                facilitiesTmp.shift() // use filter
            }
            console.log("facilitiesBefore", facilitiesTmp);
            let facilitiesTmp2 = [];
            if (ordersSelect) {
                ordersSelect.forEach((item) => {
                    let check = false;
                    // let facilitiesOrder = facilities.filter((item) => item.type = "order")
                    // neu da ton tai order trong router
                    facilitiesTmp?.forEach((faci) => {
                        if (faci.orderCode == item.orderCode) {
                            check = true;
                        }
                    })
                    // neu order chua ton tai trong lo trinh
                    if (!check) {
                        let fromFacility = {
                            code: item?.id + "F1",
                            facilityId: item?.fromFacility.facilityId,
                            facilityName: item?.fromFacility.facilityName,
                            facilityCode: item?.fromFacility.facilityCode,
                            earlyTime: item?.earlyPickupTime,
                            lateTime: item?.latePickupTime,
                            action: "PICKUP_CONTAINER",
                            orderCode: item?.orderCode,
                            orderId: item.id,
                            container: item?.containerModel,
                            containerCode: item?.containerModel.containerCode,
                            longitude: item?.fromFacility.longitude,
                            latitude: item?.fromFacility.latitude,
                            arrivalTime: null,
                            departureTime: null,
                            type: "order"
                        }
                        let toFacility = {
                            code: item?.id + "F2",
                            facilityId: item?.toFacility.facilityId,
                            facilityName: item?.toFacility.facilityName,
                            facilityCode: item?.toFacility.facilityCode,
                            earlyTime: item?.earlyDeliveryTime,
                            lateTime: item?.lateDeliveryTime,
                            action: "DELIVERY_CONTAINER",
                            orderCode: item?.orderCode,
                            orderId: item.id,
                            container: item?.containerModel,
                            containerCode: item?.containerModel.containerCode,
                            longitude: item?.toFacility.longitude,
                            latitude: item?.toFacility.latitude,
                            arrivalTime: null,
                            departureTime: null,
                            type: "order"
                        }
                        facilitiesTmp.push(fromFacility);
                        facilitiesTmp.push(toFacility);
                    }
                });
                facilitiesTmp2 = facilitiesTmp;
                console.log("facilitiesTmp", facilitiesTmp);
                facilitiesTmp.forEach((faci, index) => {
                    if (faci.type == "order") {
                        let check = false;
                        ordersSelect.forEach((item) => {
                            if (item.orderCode == faci.orderCode) {
                                check = true;
                            }
                        });
                        if (!check) {
                            facilitiesTmp2 = facilitiesTmp2.filter((req) => req.orderId != faci.orderId);
                        }
                    }
                })
                console.log("facilitiesTmp2", facilitiesTmp2);
            }
            setFacilities(facilitiesTmp2);
            let facilitiesAdd = truckItem?.concat(facilitiesTmp2);
            setFacilitiesFinal(facilitiesAdd);
        }
    }, [ordersSelect])
    console.log("facilities151", facilitiesFinal);
    console.log("addTripItem", addTripItem);
    const handleAddTripItem = () => {
        setOpen(true);
    }
    useEffect(() => {
        let facilitiesTmp = facilities;
        if (addTripItem.length > 0) {
            facilitiesTmp = facilitiesTmp.concat(addTripItem);
            setFacilities(facilitiesTmp);
            let facilitiesFinalTmp = truckItem.concat(facilitiesTmp);
            setFacilitiesFinal(facilitiesFinalTmp);
            setAddTripItem([]);
        }
    }, [addTripItem])

    useEffect(() => {

        if (tripItems?.length > 0) {
            setFacilitiesFinal(tripItems);
            let truckItemtmp = [];
            truckItemtmp.push(tripItems[0]);
            console.log("tripUtem", tripItems[0]);
            setTruckItem(truckItemtmp);
            let facilitiesTmp = tripItems.filter((item, index) => index != 0);
            // facilitiesTmp.splice(0, 1);
            setFacilities(facilitiesTmp)

        }
    }, [tripItems])
    useEffect(() => {
        setTripItem(facilitiesFinal);
    }, [facilitiesFinal]);
    console.log("tripItems", tripItems)
    return (
        <Box className="facility-arrangment">
            <Box className="facility-arrangment-text">
                <Typography>Facilities Arrangement:</Typography>
                <Button variant="contained" className="header-trip-detail-btn-save"
                    onClick={handleAddTripItem}
                >Add TripItem
                </Button>
            </Box>
            <DragDropContext onDragEnd={handleDragEnd}>
                <Droppable droppableId="droppable">
                    {(provided) => (
                        <Box ref={provided.innerRef} {...provided.droppableProps}>
                            {facilitiesFinal ? facilitiesFinal.map((item, index) => (
                                <TripItem index={index} item={item} facilities={facilitiesFinal} setFacilities={setFacilitiesFinal} />
                            )) : null}
                        </Box>
                    )}
                </Droppable>
            </DragDropContext>
            <ModalTripItem openModal={open} handleModal={handleModal} setAddTripItem={setAddTripItem} trailerSelect={trailer}
                setTrailerSelect={setTrailer} truckSelected={truckSelected} />
        </Box>
    )
}
export default OrderArrangement;