import React, { useEffect, useState } from "react";
import './styles.scss';
import { Alert, Box, Container, Divider } from "@mui/material";
import HeaderShipmentScreen from "./ShipmentScreenHeader";
import ShipmentScreenContents from "./ShipmentScreenContents";
import { getShipment } from "api/ShipmentAPI";

const ShipmentScreen = () => {
    const [shipments, setShipments] = useState([]);
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    useEffect(() => {
        getShipment({page: page, pageSize: rowsPerPage})
        .then((res) => {
            console.log("shipment==========", res.data.data)
            setShipments(res.data.data.shipmentModels);
            setCount(res.data.data.count);
        });
    }, [toastOpen, page, rowsPerPage]);
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
                <ShipmentScreenContents shipments={shipments} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>

    )
}
export default ShipmentScreen;