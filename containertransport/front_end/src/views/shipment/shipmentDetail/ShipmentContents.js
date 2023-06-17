import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, Button, Container, Divider, Icon, Typography } from "@mui/material";

const ShipmentContents = ({ shipment }) => {
    console.log("aaa", shipment)
    return (
        <Box className="shipment-detail">
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Shipment code:</Typography>
                </Box>
                <Typography>{shipment?.code}</Typography>
            </Box>
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Executed Time:</Typography>
                </Box>
                <Typography>{new Date(shipment?.executed_time).toLocaleString()}</Typography>
            </Box>
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Status:</Typography>
                </Box>
                <Typography>{shipment?.status}</Typography>
            </Box>
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Create By:</Typography>
                </Box>
                <Typography>{shipment?.created_by_user_id}</Typography>
            </Box>
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Create At:</Typography>
                </Box>
                <Typography>{new Date(shipment?.createdAt).toLocaleString()}</Typography>
            </Box>
            <Box className="shipment-detail-item">
                <Box className="shipment-detail-item-text">
                    <Typography>Description:</Typography>
                </Box>
                <Typography>{shipment?.description}</Typography>
            </Box>
        </Box>
    )
}
export default ShipmentContents;