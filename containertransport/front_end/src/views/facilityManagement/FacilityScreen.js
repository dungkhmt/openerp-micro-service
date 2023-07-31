import React, { useContext, useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import ContentsFacilityMana from "./ContentFacillityMana";
import './styles.scss';
import HeaderFacilityScreen from "./HeaderFacilityScreen";
import {getFacilityOwner } from "api/FacilityAPI";
import { MyContext } from "contextAPI/MyContext";
import SearchBar from "components/search/SearchBar";
import { facilityStatus } from "config/menuconfig";

const FacilityScreen = () => {
    const [facilities, setFacilities] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);
    const { role, preferred_username } = useContext(MyContext);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [flag, setFlag] = useState(false);

    const [openModal, setOpenModal] = useState(false);

    const [filters, setFilters] = useState([]);

    const [status, setStatus] = useState([])

    //owner: preferred_username
    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.facilityCode = code.value;
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
        }
        getFacilityOwner(data)
            .then((res) => {
                console.log("facility==========", res?.data.data.facilityModels)
                setFacilities(res?.data.data.facilityModels);
                setCount(res?.data.data.count);
            });
        console.log("role", role);
        let statusTmp = [];
        for(let [key, value] of facilityStatus.entries()) {
            statusTmp.push({name: value});
        }
        setStatus(statusTmp);
    }, [page, rowsPerPage, openModal, flag, filters]);

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    console.log("filters", filters);
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <HeaderFacilityScreen openModal={openModal} handleClose={handleClose}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <Box>
                    <SearchBar filters={filters} setFilters={setFilters} status={status} />
                </Box>
                <ContentsFacilityMana facilities={facilities} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} 
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag}/>
            </Container>
        </Box>
    );
};
export default FacilityScreen;