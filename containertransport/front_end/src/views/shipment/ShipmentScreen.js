import React, { useEffect, useState } from "react";
import './styles.scss';
import { Alert, Box, Container, Divider } from "@mui/material";
import HeaderShipmentScreen from "./ShipmentScreenHeader";
import ShipmentScreenContents from "./ShipmentScreenContents";
import { getShipment } from "api/ShipmentAPI";
import SearchBar from "components/search/SearchBar";

const ShipmentScreen = () => {
    const [shipments, setShipments] = useState([]);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [filters, setFilters] = useState([]);

    const status = [
        { name: "WAITING_SCHEDULER"},
        { name: "SCHEDULED" },
        { name: "EXECUTING" },
        { name: "DONE" }
    ]
    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.shipmentCode = code.value;
            data.page = 0;
            setPage(0);
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
            data.page = 0;
            setPage(0);
        }
        getShipment(data)
            .then((res) => {
                console.log("shipment==========", res.data.data)
                setShipments(res?.data.data.shipmentModels);
                setCount(res?.data.data.count);
            });
    }, [toastOpen, page, rowsPerPage, filters]);
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <HeaderShipmentScreen setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <Box>
                    <SearchBar filters={filters} setFilters={setFilters} status={status} type="status" />
                </Box>
                <ShipmentScreenContents shipments={shipments} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>

    )
}
export default ShipmentScreen;