import React, { useEffect, useState } from "react";
import { Box, Typography } from "@mui/material";
import './styles.scss';
import { DragDropContext } from "react-beautiful-dnd";
import { Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";


const INITIAL_LIST = [
    {
        id: '1',
        firstName: 'Robin',
        lastName: 'Wieruch',
    },
    {
        id: '2',
        firstName: 'Aiden',
        lastName: 'Kettel',
    },
    {
        id: '3',
        firstName: 'Jannet',
        lastName: 'Layn',
    },
];

const OrderArrangement = () => {
    const [facilities, setFacilities] = useState(INITIAL_LIST);

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
    return (
        <Box>
            <Box>
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
                                        >
                                            {item.firstName} {item.lastName}
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