import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import './styles.scss';
import HeaderTrailerScreen from "./HeaderTrailerScreen";
import { getTraler } from "api/TrailerAPI";
import ContentsTrailerScreen from "./ContentTrailerScreen";

const TrailerScreen = () => {
    const [trailer, setTrailer] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [openModal, setOpenModal] = useState(false);

    const [flag, setFlag] = useState(false); 

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    useEffect(() => {
        getTraler({ page: page, pageSize: rowsPerPage }).then((res) => {
            console.log("trailer", res?.data.data.trailerModels);
            setTrailer(res?.data.data.trailerModels);
            setCount(res?.data.data.count);
        })
    }, [openModal, page, rowsPerPage, flag])


    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <HeaderTrailerScreen openModal={openModal} handleClose={handleClose}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTrailerScreen trailer={trailer} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag} />
            </Container>
        </Box>
    )
}
export default TrailerScreen;