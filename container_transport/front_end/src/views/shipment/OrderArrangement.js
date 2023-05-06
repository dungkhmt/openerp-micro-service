import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Box, Typography } from "@mui/material";
import './styles.scss';
import { DragDropContext } from "react-beautiful-dnd";
import { Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";

const OrderArrangement = ({ordersSelect, setTripItem}) => {
    const [facilities, setFacilities] = useState([]);
    const [open, setOpen] = useState(false);
    open = useRef();
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
    useEffect (() => {
        let facilitiesTmp = [];
        ordersSelect.forEach((item) => {
            let fromFacility = {
                id: item?.id + "F1",
                facilityId: item?.fromFacility.facilityId,
                facilityName: item?.fromFacility.facilityName,
                facilityCode: item?.fromFacility.facilityCode,
                earlyTime: item?.earlyPickupTime,
                lateTime: item?.latePickupTime,
                action: "PICKUP",
                orderCode: item?.orderCode,
                longitude: item?.fromFacility.longitude,
                latitude: item?.fromFacility.latitude
            }
            let toFacility = {
                id: item?.id + "T2",
                facilityId: item?.toFacility.facilityId,
                facilityName: item?.toFacility.facilityName,
                facilityCode: item?.toFacility.facilityCode,
                earlyTime: item?.earlyDeliveryTime,
                lateTime: item?.lateDeliveryTime,
                action: "DELIVERY",
                orderCode: item?.orderCode,
                longitude: item?.toFacility.longitude,
                latitude: item?.toFacility.latitude
            }
            facilitiesTmp.push(fromFacility);
            facilitiesTmp.push(toFacility);
        });
        setFacilities(facilitiesTmp);
        setTripItem(facilitiesTmp);
    }, [ordersSelect])
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
                                <Draggable
                                    key={item.id}
                                    index={index}
                                    draggableId={item.id}>
                                    {(provided, snapshot) => (
                                        <Box
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
                                            <Box ref={open} onClick={() => setOpen(!open)}>{item.orderCode} - {item.facilityName} - {item.action}</Box>
                                            
                                            {open? (<Box>
                                                <Box>anhvu</Box>
                                                <Box></Box>
                                            </Box>) : null}
                                        </Box>
                                    )}
                                </Draggable>
                            ))}
                        </Box>
                    )}
                </Droppable>
            </DragDropContext>
        </Box>
    )
}
export default OrderArrangement;