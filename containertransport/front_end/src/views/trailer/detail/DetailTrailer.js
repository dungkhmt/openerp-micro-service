import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Alert, Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { useHistory, useParams } from "react-router-dom";
import { menuIconMap } from "config/menuconfig";
import { deleteTrailer, getTralerById } from "api/TrailerAPI";
import ModalTrailer from "../modal/ModalTrailer";

const DetailTrailer = () => {

    const history = useHistory();
    const { trailerId } = useParams();

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [trailer, setTrailer] = useState('');

    const [open, setOpen] = useState(false);


    useEffect(() => {
        getTralerById(trailerId).then((res) => {
            setTrailer(res?.data);
        })
    }, [open]);
    const handleClose = () => {
        setOpen(!open);
    }
    const handleDeleteTrailer = () => {
        deleteTrailer(trailerId).then((res) => {
            console.log(res);
            setToastMsg("Delete Trailer Success");
            setToastType("success");
            setToast(true);
            setTimeout(() => {
                setToast(false);
            }, "3000");
            history.push('/trailer');
        })
    }
    console.log("trailer", trailer)
    return (
        <Box className="fullScreen">
            <Container maxWidth="xl" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>

                <Box className="header-detail">
                    <Box className="headerScreen-go-back"
                        onClick={() => history.push('/trailer')}
                        sx={{ cursor: "pointer" }}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back trailer screen</Typography>
                    </Box>
                    <Box className="headerScreen-detail-info">
                        <Box className="title-header">
                            <Typography >Trailer {trailer?.trailerCode}</Typography>
                        </Box>
                        <Box className="btn-header">
                            {trailer?.status === "AVAILABLE" ? (
                                <>
                                    <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                                        onClick={handleDeleteTrailer}
                                    >Delete</Button>
                                    <Button variant="contained" className="header-submit-shipment-btn-save"
                                        onClick={handleClose}
                                    >Modify</Button>
                                </>
                            ) : null}
                        </Box>
                    </Box>
                </Box>

                <Box className="divider">
                    <Divider />
                </Box>

                <Box className="facility-info">
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Trailer Code:</Typography>
                        </Box>
                        <Typography>{trailer?.trailerCode}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility:</Typography>
                        </Box>
                        <Typography>{trailer?.facilityResponsiveDTO?.facilityCode}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility name:</Typography>
                        </Box>
                        <Typography>{trailer?.facilityResponsiveDTO?.facilityName}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Address:</Typography>
                        </Box>
                        <Typography>{trailer?.facilityResponsiveDTO?.address}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Status:</Typography>
                        </Box>
                        <Typography>{trailer?.status}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Create At:</Typography>
                        </Box>
                        <Typography>{new Date(trailer?.createdAt).toLocaleString()}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Update At:</Typography>
                        </Box>
                        <Typography>{new Date(trailer?.updatedAt).toLocaleString()}</Typography>
                    </Box>
                </Box>

                {open ? (<ModalTrailer open={open} handleClose={handleClose} trailer={trailer}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
            </Container>
        </Box>
    )
}
export default DetailTrailer;