import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import '../styles.scss';
import { DragDropContext } from "react-beautiful-dnd";
import { Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import ModalTripItem from "../tripCreate/ModalTripItem";


const TripItem = ({ index, item, facilities, setFacilities }) => {
    const [open, setOpen] = useState(false);
    const [arrivalTime, setArrivalTime] = useState(item?.arrivalTime);
    const [departureTime, setDepartureTime] = useState(item?.departureTime);

    const handleChangeTime = (time, id, type) => {
        facilities.map((facility, i) => {
            if (facility.id === item.id) {
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
    return (
        <Draggable key={item?.id} index={index} draggableId={item?.id}>
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
                    onClick={() => setOpen(!open)}
                >
                    <Box>{item?.orderCode} - {item?.facilityName} - {item?.action}</Box>

                    {open ?
                        (<Box>
                            {item?.action != "DEPART" && item?.action != "STOP" ? (
                                <Box className="trips-item-input">
                                    <Box>Container: {item?.container?.containerCode}</Box>
                                </Box>) : null}
                            {index != 0 ? (
                                < Box className="trips-item-input">
                                    <Box>Arrival Time:</Box>
                                    <Box>
                                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                                            <DemoContainer components={['DateTimePicker']}>
                                                <DateTimePicker label="Early Delivery Time"
                                                    defaultValue={item?.arrivalTime ? dayjs(new Date(item?.arrivalTime)) : null}
                                                    onChange={(e) => handleChangeTime((new Date(e)).getTime(), item?.id, "arrivalTime")}
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
                                                    onChange={(e) => handleChangeTime((new Date(e)).getTime(), item?.id, "departureTime")}
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

const OrderArrangement = ({ ordersSelect, setTripItem, truckSelected, tripId }) => {
    const [facilities, setFacilities] = useState([]);
    const [facilitiesFinal, setFacilitiesFinal] = useState([]);
    const [open, setOpen] = useState(false);
    const [addTripItem, setAddTripItem] = useState();
    const [truckItem, setTruckItem] = useState([]);

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
        if (truckSelected) {
            let startPoint = {
                id: truckSelected?.id + "S1",
                facilityId: truckSelected?.facilityResponsiveDTO?.facilityId,
                facilityName: truckSelected?.facilityResponsiveDTO?.facilityName,
                facilityCode: truckSelected?.facilityResponsiveDTO?.facilityCode,
                action: "DEPART",
                orderCode: truckSelected?.truckCode,
                longitude: truckSelected?.facilityResponsiveDTO?.longitude,
                latitude: truckSelected?.facilityResponsiveDTO?.latitude,
                arrivalTime: null,
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
    }, [truckSelected]);
    
    useEffect(() => {
        let facilitiesTmp = facilitiesFinal;
        console.log("facilitiesBefore111", truckItem);
        if (truckItem.length > 0){
            facilitiesTmp.shift()
        }
        console.log("facilitiesBefore", facilitiesTmp);
        let facilitiesTmp2 = [];
        if (ordersSelect) {
            ordersSelect.forEach((item) => {
                let check = false;
                // let facilitiesOrder = facilities.filter((item) => item.type = "order")
                // neu da ton tai order trong router
                facilitiesTmp?.forEach((faci) => {
                    if (faci.orderId == item.id) {
                        check = true;
                    }
                })
                // neu order chua ton tai trong lo trinh
                if (!check) {
                    let fromFacility = {
                        id: item?.id + "F1",
                        facilityId: item?.fromFacility.facilityId,
                        facilityName: item?.fromFacility.facilityName,
                        facilityCode: item?.fromFacility.facilityCode,
                        earlyTime: item?.earlyPickupTime,
                        lateTime: item?.latePickupTime,
                        action: "PICKUP-CONTAINER",
                        orderCode: item?.orderCode,
                        orderId: item.id,
                        container: item?.containers[0],
                        longitude: item?.fromFacility.longitude,
                        latitude: item?.fromFacility.latitude,
                        arrivalTime: null,
                        departureTime: null,
                        type: "order"
                    }
                    let toFacility = {
                        id: item?.id + "F2",
                        facilityId: item?.toFacility.facilityId,
                        facilityName: item?.toFacility.facilityName,
                        facilityCode: item?.toFacility.facilityCode,
                        earlyTime: item?.earlyDeliveryTime,
                        lateTime: item?.lateDeliveryTime,
                        action: "DELIVERY-CONTAINER",
                        orderCode: item?.orderCode,
                        orderId: item.id,
                        container: item?.containers[0],
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
                if(faci.type == "order") {
                    let check = false;
                    ordersSelect.forEach((item) => {
                        if (item.id == faci.orderId) {
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
        if (!tripId) {
            setTripItem(facilitiesAdd)
        }

    }, [ordersSelect])
    console.log("facilities151", facilitiesFinal);
    console.log("truckSelected", truckSelected);
    const handleAddTripItem = () => {
        setOpen(true);
    }
    // useEffect(() => {
    //     let facilitiesTmp = facilitiesFinal
    //     if (addTripItem) {
    //         facilitiesTmp = facilitiesTmp.concat(addTripItem);
    //         console.log("facilitiesTmp", facilitiesTmp);
    //         setFacilitiesFinal(facilitiesTmp);
    //     }
    // }, addTripItem)
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
            <ModalTripItem openModal={open} handleModal={handleModal} setAddTripItem={setAddTripItem} />
        </Box>
    )
}
export default OrderArrangement;