import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Alert, Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { useHistory, useLocation, useParams } from "react-router-dom";
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { MyContext } from "contextAPI/MyContext";
import TripsContents from "./TripsContents";
import { autoCreateRouter, getShipmentById } from "api/ShipmentAPI";
import ShipmentContents from "./ShipmentContents";
import { getTrips } from "api/TripAPI";
import ReactLoading from "react-loading";
import ModalShipment from "../ModalShipment";

const ShipmentDetail = () => {
    const history = useHistory();
    const { shipmentId } = useParams();
    const [shipment, setShipment] = useState();
    const [trips, setTrips] = useState([]);
    const [loading, setLoading] = useState(false);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [open, setOpen] = useState(false);

    const [flag, setFlag] = useState(false);

    useEffect(() => {
        let data = {
            shipmentId: shipmentId
        }
        getTrips(data).then((res) => {
            setTrips(res?.data.data);
        });

        getShipmentById(shipmentId).then((res) => {
            setShipment(res?.data.data);
        });


    }, [open, flag]);

    const autoCreateTrip = () => {
        setLoading(true);
        autoCreateRouter(shipmentId).then((res) => {
            setLoading(false);
            setFlag(!flag)
        })
            .catch((error) => {
                console.log(error);
            })
    }
    const handleModifyShipment = () => {
        setOpen(!open);
    }
    console.log("=========", shipment)
    return (
        <Box className="fullScreen">
            {loading ? (
                <ReactLoading type="spin" color="#0000FF" className="loading"
                    height='6%' width='6%' />
            ) : (
                <Container maxWidth="100vw" className="container">
                    <Box className="toast">
                        {toastOpen ? (
                            <Alert variant="filled" severity={toastType} >
                                <strong>{toastMsg}</strong >
                            </Alert >) : null}
                    </Box>
                    <Box className="headerScreen-trip-detail">
                        <Box className="headerScreen-trip-detail-go-back"
                            onClick={() => history.push('/shipment')}
                            sx={{ cursor: "pointer" }}
                        >
                            <Icon>
                                {menuIconMap.get("ArrowBackIosIcon")}
                            </Icon>
                            <Typography>Go back shipment screen</Typography>
                        </Box>
                        <Box className="headerScreen-trip-detail-info">
                            <Box className="title-header">
                                <Typography >Shipment {shipment?.code}</Typography>
                            </Box>
                            {/* <Box className="btn-add"
                            onClick={() => history.push('/shipment/trip/create')}
                        >
                        </Box> */}
                            <Box className="btn-header">
                                <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                                // onClick={handleCancelCreateShipment}
                                >Delete</Button>
                                <Button variant="contained" className="header-submit-shipment-btn-save"
                                onClick={handleModifyShipment}
                                >Modify</Button>
                            </Box>
                        </Box>
                    </Box>
                    <Box className="divider">
                        <Divider />
                    </Box>
                    <Box className="title">
                        <Typography>Shipment Info</Typography>
                    </Box>
                    <ShipmentContents shipment={shipment} />
                    <Box className="trips-mana">
                        <Box className="title">
                            <Typography>Trips Management</Typography>
                        </Box>
                        <Box className="trips-btn">
                            {/* {trips.length > 0 ? */}
                            <Box className="auto-create-trips"
                                onClick={autoCreateTrip}
                            >
                                <PrimaryButton className="btn-header">
                                    <Icon className="icon">
                                        {menuIconMap.get("AutoFixHighIcon")}
                                    </Icon>
                                    <Typography>
                                        Auto Create Trips
                                    </Typography>
                                </PrimaryButton>
                            </Box>
                            <Box className="btn-add"
                                onClick={() => history.push({
                                    pathname: `/shipment/trip/create/${shipmentId}`,
                                })}
                            >
                                <PrimaryButton className="btn-header">
                                    <Icon className="icon">
                                        {menuIconMap.get("ControlPointIcon")}
                                    </Icon>
                                    <Typography>
                                        New Trip
                                    </Typography>
                                </PrimaryButton>
                            </Box>
                        </Box>

                    </Box>
                    {trips.length > 0 ? 
                    <TripsContents trips={trips} shipmentId={shipmentId}
                        setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} flag={flag} setFlag={setFlag}
                    /> : null}

                    {open ? (
                        <ModalShipment open={open} setOpen={setOpen} shipment={shipment}
                        setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                    ) : null}
                </Container>
            )}
        </Box>
    )
}
export default ShipmentDetail;