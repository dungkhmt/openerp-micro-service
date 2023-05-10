import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Box, Typography } from "@mui/material";
import './styles.scss';
import { DragDropContext } from "react-beautiful-dnd";
import { Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";


const TripItem = ({ index, item, facilities, setFacilities }) => {
    const [open, setOpen] = useState(false);
    const [arrivalTime, setArrivalTime] = useState(item.arrivalTime);
    const handleChangeTime = (time, id) => {
        console.log("id", id);
        console.log("index", item.id);
        facilities.map((facility, i) => {
        if (facility.id === item.id) {

            facility.arrivalTime = time;
            console.log("facility", dayjs(new Date(time)));
            return facility;
        } else {
          // The rest haven't changed
          return facility;
        }
      });
    //   setCounters(nextCounters);
    }
    return (
        <Draggable key={item.id} index={index} draggableId={item.id}>
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
                    <Box onClick={() => setOpen(!open)}>{item.orderCode} - {item.facilityName} - {item.action}</Box>

                    {open ?
                        (<Box>
                            <Box className="trips-item-input">
                                <Box>Arrival Time</Box>
                                <Box>
                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DemoContainer components={['DateTimePicker']}>
                                            <DateTimePicker label="Early Delivery Time"
                                            defaultValue={item.arrivalTime ? dayjs(new Date(item.arrivalTime)) : null}
                                            onChange={(e) => handleChangeTime((new Date(e)).getTime(), item.id)}
                                            />
                                        </DemoContainer>
                                    </LocalizationProvider>
                                </Box>
                            </Box>
                            <Box className="trips-item-input">
                                <Box>Departure Time</Box>
                                <Box>
                                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                                        <DemoContainer components={['DateTimePicker']}>
                                            <DateTimePicker label="Early Delivery Time"
                                            // onChange={(e) => setEarlyDeliveryTime((new Date(e)).getTime())}
                                            />
                                        </DemoContainer>
                                    </LocalizationProvider>
                                </Box>
                            </Box>
                        </Box>) : null}
                </div>
            )}
        </Draggable>
    )
}

const OrderArrangement = ({ ordersSelect, setTripItem }) => {
    const [facilities, setFacilities] = useState([]);
    const [open, setOpen] = useState(false);

    const reorder = (list, startIndex, endIndex) => {
        const result = Array.from(list);
        const [removed] = result.splice(startIndex, 1);
        result.splice(endIndex, 0, removed);

        return result;
    };
    const handleDragEnd = ({ destination, source }) => {
        // reorder list
        if (!destination) return;

        setFacilities(reorder(facilities, source.index, destination.index));
    };
    useEffect(() => {
        let facilitiesTmp = [];
        ordersSelect.forEach((item) => {
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
                longitude: item?.fromFacility.longitude,
                latitude: item?.fromFacility.latitude,
                arrivalTime: null,
                departureTime: null
            }
            let toFacility = {
                id: item?.id + "T2",
                facilityId: item?.toFacility.facilityId,
                facilityName: item?.toFacility.facilityName,
                facilityCode: item?.toFacility.facilityCode,
                earlyTime: item?.earlyDeliveryTime,
                lateTime: item?.lateDeliveryTime,
                action: "DELIVERY-CONTAINER",
                orderCode: item?.orderCode,
                orderId: item.id,
                longitude: item?.toFacility.longitude,
                latitude: item?.toFacility.latitude,
                arrivalTime: null,
                departureTime: null
            }
            facilitiesTmp.push(fromFacility);
            facilitiesTmp.push(toFacility);
        });
        setFacilities(facilitiesTmp);
        setTripItem(facilitiesTmp);
    }, [ordersSelect])
    console.log("facilities", facilities);
    return (
        <Box className="facility-arrangment">
            <Box className="facility-arrangment-text">
                <Typography>Facilities Arrangement:</Typography>
            </Box>
            <DragDropContext onDragEnd={handleDragEnd}>
                <Droppable droppableId="droppable">
                    {(provided) => (
                        <Box ref={provided.innerRef} {...provided.droppableProps}>
                            {facilities.map((item, index) => (
                                <TripItem index={index} item={item} facilities={facilities} setFacilities={setFacilities}/>
                                // <Draggable
                                //     key={item.id}
                                //     index={index}
                                //     draggableId={item.id}>
                                //     {(provided, snapshot) => (
                                //         <Box
                                //             ref={provided.innerRef}
                                //             {...provided.draggableProps}
                                //             {...provided.dragHandleProps}
                                //             style={{
                                //                 // default item style
                                //                 padding: '8px 16px',
                                //                 // default drag style
                                //                 ...provided.draggableProps.style,
                                //                 // customized drag style
                                //                 background: snapshot.isDragging
                                //                     ? 'lightblue'
                                //                     : 'transparent',
                                //                 borderRadius: snapshot.isDragging
                                //                     ? '8px' : '0'
                                //             }}
                                //             className="item-facility"
                                //         >
                                //             <TripItem index={index} item={item} />
                                //             <Box 
                                //             onClick={() => setOpen(!open)}
                                //             >{item.orderCode} - {item.facilityName} - {item.action}</Box>

                                //             {open ? (<Box>
                                //                 <Box>anhvu</Box>
                                //                 <Box></Box>
                                //             </Box>) : null}
                                //         </Box>
                                //     )}
                                // </Draggable>
                            ))}
                        </Box>
                    )}
                </Droppable>
            </DragDropContext>
        </Box>
    )
}
export default OrderArrangement;