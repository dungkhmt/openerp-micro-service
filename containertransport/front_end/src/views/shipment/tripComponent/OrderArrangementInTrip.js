import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Box, Button, Icon, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
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
import { menuIconMap, tripItemType } from "config/menuconfig";
import { SortableContainer, SortableHandle, SortableElement, arrayMove } from 'react-sortable-hoc'


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
                    <TableRow>
                        <TableCell>
                            {item?.orderCode}
                        </TableCell>
                        <TableCell>
                            <Box className="item-facility-line">
                                <Box onClick={() => setOpen(!open)}>
                                    {item?.orderCode} - {item?.facilityName} - {item?.action}
                                </Box>
                                {(item?.type === "Trailer" || (item?.type === "Truck" && item?.action === "STOP")) ? (
                                    <Box onClick={handleDeleteTripItem}>
                                        <Icon>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                                    </Box>) : <Box></Box>}
                            </Box>
                        </TableCell>
                    </TableRow>


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

const headCells = [
    {
        id: 'drag',
        numeric: false,
        disablePadding: false,
        label: '',
        width: '10%'
    },
    {
        id: 'code',
        numeric: false,
        disablePadding: false,
        label: 'Code',
        width: '15%'
    },
    {
        id: 'facility',
        numeric: false,
        disablePadding: false,
        label: 'Facility',
        width: '15%'
    },
    {
        id: 'action',
        numeric: false,
        disablePadding: false,
        label: 'Action',
        width: '20%'
    },
    {
        id: 'entity',
        numeric: false,
        disablePadding: false,
        label: 'Entity',
        width: '15%'
    },
    {
        id: 'status',
        numeric: false,
        disablePadding: false,
        label: 'Status',
        width: '20%'
    },
    {
        id: 'impl',
        numeric: false,
        disablePadding: false,
        label: '',
        width: '5%'
    },
];

function EnhancedTableHead(props) {
    const { onSelectAllClick, order, orderBy, numSelected, rowCount } = props;

    return (
        <TableHead>
            <TableRow>
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        align={headCell.numeric ? 'right' : 'left'}
                        padding={headCell.disablePadding ? 'none' : 'normal'}
                        // sortDirection={orderBy === headCell.id ? order : false}
                        width={headCell?.width}
                    >
                        {headCell.label}
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

const TableBodySortable = SortableContainer(({ children, displayRowCheckbox }) => (
    <TableBody displayRowCheckbox={displayRowCheckbox} >
        {children}
    </TableBody >
))

const DragHandle = SortableHandle(({ style }) => (
    <span style={{ ...style, ...{ cursor: 'move' } }}> {':::'} </span>)
)

const Row = SortableElement(({ data, facilitiesTmp, setFacilitiesTmp,...other }) => {
    const handleDeleteTripItem = (code) => {
        let facis = facilitiesTmp.filter((tr) => tr?.code !== code);
        setFacilitiesTmp(facis);
    }
    return (
        <TableRow {...other}>
            {/* { other.children[0]} */}
            <TableCell style={{ width: '5%' }} >
                <DragHandle />
            </TableCell>
            <TableCell>
                {data.orderCode}
            </TableCell>
            <TableCell>
                {data.facilityCode}
            </TableCell>
            <TableCell>
                {data.action}
            </TableCell>
            {data?.type === tripItemType.get("Truck") ? (<TableCell>{data?.orderCode}</TableCell>) : null}
            {data?.type === tripItemType.get("Trailer") ? (<TableCell>{data?.trailerCode}</TableCell>) : null}
            {data?.type === tripItemType.get("Order") ? (<TableCell>{data?.containerCode}</TableCell>) : null}
            <TableCell>
                {data.status ? data.status : "SCHEDULED"}
            </TableCell>
            {(data?.type === "Trailer" || (data?.type === "Truck" && data?.action === "STOP")) ? (
                <TableCell>
                    <Box className="icon-view-screen"
                    onClick={() => handleDeleteTripItem(data.code)}
                    >
                        <Icon>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                    </Box>
                </TableCell>) : 
                <TableCell><Box></Box></TableCell>}
        </TableRow >
    )
})

const OrderArrangementInTrip = ({ ordersSelect, setTripItem, truckSelected, tripItems, flag }) => {
    // const [facilities, setFacilities] = useState([]);
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
                console.log("truckSelected", truckSelected);
                facilitiesTmp = facilitiesFinal.filter((faci) => (faci.action !== "DEPART"));
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
                    type: tripItemType.get("Truck")
                }
                facilitiesTmp.unshift(startPoint);
                setTruckItem([startPoint]);
                // facilitiesTmp = facilitiesTmp.concat(facilities);
                facilitiesTmp = updateSeqInTrip(facilitiesTmp);
                setFacilitiesFinal(facilitiesTmp);
            }
            if (!truckSelected) {
                facilitiesTmp = facilitiesFinal.filter((faci) => (faci.action !== "DEPART"));
                facilitiesTmp = facilitiesTmp.filter((faci) => (faci.action !== "STOP"));
                // setFacilities(facilitiesTmp);
                facilitiesTmp = updateSeqInTrip(facilitiesTmp);
                setFacilitiesFinal(facilitiesTmp);
                setTruckItem([]);
            }
        }
    }, [truckSelected]);

    useEffect(() => {
        let facilitiesTmp = facilitiesFinal;
        if (flag) {
            // if (truckItem.length > 0) {
            //     facilitiesTmp.shift() // use filter
            // }
            console.log("facilitiesBefore", facilitiesTmp);
            let facilitiesTmp2 = [];
            if (ordersSelect) {
                ordersSelect.forEach((item) => {
                    let check = false;
                    console.log("item", item)
                    // let facilitiesOrder = facilities.filter((item) => item.type = "order")
                    // neu da ton tai order trong router
                    facilitiesTmp?.forEach((faci) => {
                        if (faci.orderCode === item.orderCode) {
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
                            action: "PICKUP-CONTAINER",
                            orderCode: item?.orderCode,
                            orderId: item.id,
                            containerId: item?.containerModel?.id,
                            containerCode: item?.containerModel?.containerCode,
                            longitude: item?.fromFacility.longitude,
                            latitude: item?.fromFacility.latitude,
                            arrivalTime: null,
                            departureTime: null,
                            type: tripItemType.get("Order")
                        }
                        let toFacility = {
                            code: item?.id + "F2",
                            facilityId: item?.toFacility.facilityId,
                            facilityName: item?.toFacility.facilityName,
                            facilityCode: item?.toFacility.facilityCode,
                            earlyTime: item?.earlyDeliveryTime,
                            lateTime: item?.lateDeliveryTime,
                            action: "DELIVERY-CONTAINER",
                            orderCode: item?.orderCode,
                            orderId: item.id,
                            containerId: item?.containerModel?.id,
                            containerCode: item?.containerModel.containerCode,
                            longitude: item?.toFacility.longitude,
                            latitude: item?.toFacility.latitude,
                            arrivalTime: null,
                            departureTime: null,
                            type: tripItemType.get("Order")
                        }
                        facilitiesTmp.push(fromFacility);
                        facilitiesTmp.push(toFacility);
                    }
                });
                facilitiesTmp2 = facilitiesTmp;
                console.log("facilitiesTmp", facilitiesTmp);
                facilitiesTmp.forEach((faci) => {
                    if (faci.type === "Order") {
                        let check = false;
                        ordersSelect.forEach((item) => {
                            if (item.orderCode === faci.orderCode) {
                                check = true;
                            }
                        });
                        if (!check) {
                            facilitiesTmp2 = facilitiesTmp2.filter((req) => req.orderId !== faci.orderId);
                        }
                    }
                })
                console.log("facilitiesTmp2", facilitiesTmp2);
            }
            // setFacilities(facilitiesTmp2);
            // let facilitiesAdd = truckItem?.concat(facilitiesTmp2);
            facilitiesTmp2 = updateSeqInTrip(facilitiesTmp2);
            setFacilitiesFinal(facilitiesTmp2);
        }
    }, [ordersSelect])
    console.log("facilities151", facilitiesFinal);
    console.log("addTripItem", addTripItem);
    const handleAddTripItem = () => {
        setOpen(true);
    }
    useEffect(() => {
        let facilitiesTmp = facilitiesFinal;

        // console.log("fa", facilities);
        if (addTripItem.length > 0) {
            console.log("add", addTripItem);
            facilitiesTmp = facilitiesTmp.concat(addTripItem);
            // setFacilities(facilitiesTmp);
            // let facilitiesFinalTmp = truckItem.concat(facilitiesTmp);
            facilitiesTmp = updateSeqInTrip(facilitiesTmp);
            setFacilitiesFinal(facilitiesTmp);
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
            // let facilitiesTmp = tripItems.filter((item, index) => index != 0);
            // facilitiesTmp.splice(0, 1);
            // setFacilities(facilitiesTmp)

        }
    }, [tripItems])
    useEffect(() => {
        setTripItem(facilitiesFinal);
    }, [facilitiesFinal]);

    const onSortEnd = ({ oldIndex, newIndex }) => {
        let facilitiesFinalTmp = arrayMove(facilitiesFinal, oldIndex, newIndex);
        facilitiesFinalTmp = updateSeqInTrip(facilitiesFinalTmp);
        setFacilitiesFinal(facilitiesFinalTmp);

    };
    const updateSeqInTrip = (facilitiesFinalTmp) => {
        facilitiesFinalTmp = facilitiesFinalTmp.map((item, index) => {

            item['seq'] = (index + 1);
            return item;
        });
        return facilitiesFinalTmp;
    }
    console.log("ordersSelect", ordersSelect)
    return (
        <Box className="facility-arrangment-v2">
            <Box className="facility-arrangment-head">
                <Box className="facility-arrangment-head-title">
                    <Typography>Facilities Arrangement:</Typography>
                </Box>
                <Button variant="contained" className="header-trip-detail-btn-save"
                    onClick={handleAddTripItem}
                >Add TripItem
                </Button>
            </Box>
            <Table className="table-trip-items">
                <EnhancedTableHead
                    rowCount={facilitiesFinal.length}
                />
                <TableBodySortable onSortEnd={onSortEnd} useDragHandle displayRowCheckbox={false}>
                    {facilitiesFinal ? facilitiesFinal.map((row, index) => {
                        if (row.action === "DEPART") {
                            return (
                                <TableRow >
                                    <TableCell style={{ width: '5%' }} >
                                        <DragHandle />
                                    </TableCell>
                                    <TableCell>
                                        {row.orderCode}
                                    </TableCell>
                                    <TableCell>
                                        {row.facilityCode}
                                    </TableCell>
                                    <TableCell>
                                        {row.action}
                                    </TableCell>
                                    {row?.type === tripItemType.get("Truck") ? (<TableCell>{row?.orderCode}</TableCell>) : null}
                                    {row?.type === tripItemType.get("Trailer") ? (<TableCell>{row?.trailerCode ? row?.trailerCode : row?.orderCode}</TableCell>) : null}
                                    {row?.type === tripItemType.get("Order") ? (<TableCell>{row?.orderCode}</TableCell>) : null}
                                    <TableCell>
                                        {row.status ? row.status : "SCHEDULED"}
                                    </TableCell>
                                    <TableCell><Box></Box></TableCell>
                                </TableRow >
                            )
                        }
                        else {
                            return (
                                <Row
                                    index={index}
                                    key={row.id}
                                    data={row}
                                    facilitiesTmp={facilitiesFinal}
                                    setFacilitiesTmp={setFacilitiesFinal}
                                />
                            )
                        }
                    }) : null}
                </TableBodySortable>
            </Table>
            <ModalTripItem openModal={open} handleModal={handleModal} setAddTripItem={setAddTripItem} trailerSelect={trailer}
                setTrailerSelect={setTrailer} truckSelected={truckSelected} />
        </Box>
    )
}
export default OrderArrangementInTrip;