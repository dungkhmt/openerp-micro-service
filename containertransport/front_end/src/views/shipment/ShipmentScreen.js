import React, { useEffect, useState } from "react";
import './styles.scss';
import { Box, Container, Divider } from "@mui/material";
import HeaderShipmentScreen from "./ShipmentScreenHeader";
import { request } from "api";
import ShipmentScreenContents from "./ShipmentScreenContents";

const ShipmentScreen = () => {
    const [shipments, setShipments] = useState([]);

    useEffect(() => {
        request(
          "post",
          `/shipment/`, {},{},{},{},
        ).then((res) => {
          console.log("shipment==========", res.data.data)
          setShipments(res.data.data);
        });
      }, []);
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderShipmentScreen />
                <Box className="divider">
                    <Divider />
                </Box>
                <ShipmentScreenContents shipments={shipments}/>
            </Container>
        </Box>

    )
}
export default ShipmentScreen;