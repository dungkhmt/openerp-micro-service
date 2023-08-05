import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Alert, Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { useHistory, useParams } from "react-router-dom";
import { menuIconMap } from "config/menuconfig";
import { deleteTruck, getTruckById, getTrucks } from "api/TruckAPI";
import ModalTruck from "../ModalTruck";

const TruckDetail = () => {
    const history = useHistory();
    const { truckId } = useParams();
    const [truck, setTruck] = useState();

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [open, setOpen] = useState(false);


    useEffect(() => {
        getTruckById(truckId).then((res) => {
            setTruck(res.data);
        })
    }, [open]);
    const handleClose = () => {
        setOpen(!open);
    }
    const handleDeleteTruck = () => {
        deleteTruck(truckId).then((res) => {
            console.log(res);
            setToastMsg("Delete Truck Success");
            setToastType("success");
            setToast(true);
            setTimeout(() => {
                setToast(false);
            }, "3000");
            history.push('/truck');
        })
    }
    console.log("Truck", truck)
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
                        onClick={() => history.push('/truck')}
                        sx={{ cursor: "pointer" }}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back Truck screen</Typography>
                    </Box>
                    <Box className="headerScreen-detail-info">
                        <Box className="title-header">
                            <Typography >Truck {truck?.truckCode}</Typography>
                        </Box>
                        <Box className="btn-header">
                            {truck?.status === "AVAILABLE" ? (
                                <>
                                    <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                                        onClick={handleDeleteTruck}
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

                {/* <Box className="title">
                    <Typography>Truck Info</Typography>
                </Box> */}
                <Box className="facility-info">
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Truck Code:</Typography>
                        </Box>
                        <Typography>{truck?.truckCode}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Driver Name:</Typography>
                        </Box>
                        <Typography>{truck?.driverName}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Status:</Typography>
                        </Box>
                        <Typography>{truck?.status}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Facility Name:</Typography>
                        </Box>
                        <Typography>{truck?.facilityResponsiveDTO.facilityName}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>License Plates:</Typography>
                        </Box>
                        <Typography>{truck?.licensePlates}</Typography>
                    </Box>

                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Brand:</Typography>
                        </Box>
                        <Typography>{truck?.brandTruck}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Year Of Manufacture:</Typography>
                        </Box>
                        <Typography>{truck?.yearOfManufacture}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Create At:</Typography>
                        </Box>
                        <Typography>{new Date(truck?.createdAt).toLocaleString()}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Update At:</Typography>
                        </Box>
                        <Typography>{new Date(truck?.updatedAt).toLocaleString()}</Typography>
                    </Box>
                </Box>
                {open ? (<ModalTruck openModal={open} handleClose={handleClose} truckId={truckId} truck={truck}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
            </Container>
        </Box>
    )
}
export default TruckDetail;