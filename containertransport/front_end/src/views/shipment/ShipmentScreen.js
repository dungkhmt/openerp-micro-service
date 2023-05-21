import React, { useEffect, useState } from "react";
import './styles.scss';
import { Alert, Box, Container, Divider } from "@mui/material";
import HeaderShipmentScreen from "./ShipmentScreenHeader";
import { request } from "api";
import ShipmentScreenContents from "./ShipmentScreenContents";

const ShipmentScreen = () => {
    const [shipments, setShipments] = useState([]);
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();

    useEffect(() => {
        request(
            "post",
            `/shipment/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("shipment==========", res.data.data)
            setShipments(res.data.data);
        });
    }, [toastOpen]);
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        toastType === "success" ? (
                            <Alert variant="filled" severity={toastType} >
                                <strong> Created Order Success !!!</strong >
                            </Alert >
                        ) : (
                            <Alert variant="filled" severity={toastType} >
                                <strong> Created Order False !!!</strong >
                            </Alert >
                        )) : null
                    }
                </Box>
                <HeaderShipmentScreen setToast={setToast} setToastType={setToastType} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ShipmentScreenContents shipments={shipments} />
            </Container>
        </Box>

    )
}
export default ShipmentScreen;