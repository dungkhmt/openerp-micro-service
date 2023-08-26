import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Icon, Typography } from "@mui/material";
import '../styles.scss';
import {getTypeContainer } from "api/ContainerAPI";
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import ListTypeContainer from "./ListTypeContainer";
import ModalTypeContainer from "./ModalTypeContainer";

const TypeContainer = () => {
    const [typeContainers, setTypeContainers] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [openModal, setOpenModal] = useState(false);

    useEffect(() => {
        getTypeContainer({ page: page, pageSize: rowsPerPage })
            .then((res) => {
                console.log("container==========", res?.data.data.typeContainers);
                setTypeContainers(res?.data.data.typeContainers);
                setCount(res?.data.data.count);
            });
    }, [openModal, page, rowsPerPage])

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <Box className="headerScreen">
                    <Box className="title">
                        <Typography >Type Container Management</Typography>
                    </Box>
                    {/* <Box className="btn-add"
                        onClick={() => handleClose()}
                    >
                        <PrimaryButton className="btn-header">
                            <Icon className="icon">
                                {menuIconMap.get("ControlPointIcon")}
                            </Icon>
                            <Typography>
                                New Type
                            </Typography>
                        </PrimaryButton>
                    </Box> */}
                    {openModal ? (<ModalTypeContainer open={openModal} handleClose={handleClose}
                        setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
                </Box>
                <Box className="divider">
                    <Divider />
                </Box>
                <ListTypeContainer typeContainers={typeContainers} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>
    )
}
export default TypeContainer;