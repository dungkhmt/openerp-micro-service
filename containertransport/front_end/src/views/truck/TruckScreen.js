import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import HeaderTruckScreen from "./HeaderTruckScreen";
import './styles.scss';
import ContentsTruckManagement from "./ContentTruckManagement";
import { getTrucks } from "api/TruckAPI";
import SearchBar from "../../components/search/SearchBar";

const TruckScreen = () => {

    const [trucks, setTrucks] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [filters, setFilters] = useState([]);

    const [flag, setFlag] = useState(false);

    const status = [
        { name: "AVAILABLE" },
        { name: "SCHEDULED" },
        { name: "EXECUTING" }
    ]

    const handleClose = () => {
        setOpenModal(!openModal);
    }

    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.truckCode = code.value;
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
        }
        getTrucks(data).then((res) => {
            console.log("truck==========", res?.data.truckModels)
            setTrucks(res?.data.truckModels);
            setCount(res?.data.count);
        });
    }, [openModal, page, rowsPerPage, flag]);

    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.truckCode = code.value;
            data.page = 0;
            setPage(0);
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
            data.page = 0;
            setPage(0);
        }
        getTrucks(data).then((res) => {
            console.log("truck==========", res?.data.truckModels)
            setTrucks(res?.data.truckModels);
            setCount(res?.data.count);
        });
    }, [filters]);
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <HeaderTruckScreen openModal={openModal} handleClose={handleClose}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <Box>
                    <SearchBar filters={filters} setFilters={setFilters} status={status} type="status" />
                </Box>
                <ContentsTruckManagement trucks={trucks} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag} />
            </Container>
        </Box>
    );
};
export default TruckScreen;