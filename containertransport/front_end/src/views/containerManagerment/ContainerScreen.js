import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import './styles.scss';
import HeaderContainerMana from "./HeaderContainerMana";
import ContentsContainerMana from "./ContentsContainerMana";
import { getContainers } from "api/ContainerAPI";

const ContainerScreen = () => {
    const [containers, setContainers] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [openModal, setOpenModal] = useState(false);

    useEffect(() => {
        getContainers({ page: page, pageSize: rowsPerPage })
            .then((res) => {
                console.log("container==========", res?.data.data.containerModels);
                setContainers(res?.data.data.containerModels);
                setCount(res?.data.data.count);
            });
    }, [openModal, page, rowsPerPage])

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
                <HeaderContainerMana openModal={openModal} handleClose={handleClose}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsContainerMana containers={containers} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>
    )
}
export default ContainerScreen;