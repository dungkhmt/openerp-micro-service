import React, { useEffect, useState } from "react";
import './styles.scss';
import { Box, Container, Divider } from "@mui/material";
import HeaderShipmentScreen from "./HeaderShipmentScreen";

const ShipmentScreen = () => {
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderShipmentScreen />
                <Box className="divider">
                    <Divider />
                </Box>
            </Container>
        </Box>

    )
}
export default ShipmentScreen;