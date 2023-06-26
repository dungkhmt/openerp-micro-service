import React, { useContext, useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import ContentsFacilityMana from "./ContentFacillityMana";
import './styles.scss';
import HeaderFacilityScreen from "./HeaderFacilityScreen";
import { getFacility } from "api/FacilityAPI";
import { MyContext } from "contextAPI/MyContext";

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

    //owner: preferred_username
    useEffect(() => {
        getFacility({ page: page, pageSize: rowsPerPage })
            .then((res) => {
                console.log("facility==========", res?.data.data.facilityModels)
                setFacilities(res?.data.data.facilityModels);
                setCount(res.data.data.count);
            });
        console.log("role", role);
    }, [page, rowsPerPage, openModal, flag]);

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
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
                <ContentsFacilityMana facilities={facilities} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} 
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag}/>
            </Container>
        </Box>
    );
};
export default FacilityScreen;