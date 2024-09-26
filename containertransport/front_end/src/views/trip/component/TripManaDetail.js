import React, { useEffect, useState } from "react";
import { Alert, Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import '../styles.scss';
import { useHistory, useParams } from "react-router-dom";
import { getTripByTripId } from "api/TripAPI";
import { menuIconMap } from "config/menuconfig";
import { getTripItemByTripId } from "api/TripItemAPI";
import TableOrder from "./TableOrder";
import MapComponent from "views/shipment/routing/Map";
import { convertMillisecondsToHours } from "utils/Utils";

const TripManaDetail = () => {
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [executed, setExecutes] = useState(false);
 
    const history = useHistory();

    const { tripId, type } = useParams();

    const [tripItems, setTripItems] = useState([]);
    const [trip, setTrip] = useState('');

    useEffect(() => {
        if (tripId) {
            getTripByTripId(tripId).then((res) => {
                setTrip(res?.data?.data)
            });
            getTripItemByTripId(tripId).then((res) => {
                setTripItems(res?.data.data);
            })
        }
    }, [executed])
    const goBack = () => {
        if(type === "Done") {
            history.push('/trip/executed')
        }
        else {
            history.push('/trip/pending')
        }
    }

    console.log("tripItem", tripItems);
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>

                <Box className="header-detail">
                    <Box className="headerScreen-go-back"
                        onClick={goBack}
                        sx={{ cursor: "pointer" }}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back Trip screen</Typography>
                    </Box>
                    <Box className="headerScreen-detail-info">
                        <Box className="title">
                            <Typography>Trip {trip?.code}</Typography>
                        </Box>
                        <Box className="btn-header">
                            {trip?.status === "SCHEDULED" ? (
                                <Button variant="contained" className="header-create-shipment-btn-cancel"
                                    // onClick={handleDeleteTruck}
                                >EXECUTING</Button>
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
                            <Typography>Total Orders:</Typography>
                        </Box>
                        <Typography>{trip?.orderIds?.length}</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Total Distant:</Typography>
                        </Box>
                        <Typography>{Number(trip?.total_distant / 1000).toFixed(2)} (km)</Typography>
                    </Box>
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Total Time:</Typography>
                        </Box>
                        <Typography>{convertMillisecondsToHours(trip?.total_time)}</Typography>
                    </Box>
                    
                    <Box className="facility-info-item">
                        <Box className="facility-info-item-text">
                            <Typography>Start executed time:</Typography>
                        </Box>
                        <Typography>{new Date(trip?.startTime).toLocaleString()}</Typography>
                    </Box>
                </Box>

                <Box className="trip-items">
                    <TableOrder tripItems={tripItems} setExecutes={setExecutes} executed={executed} type={type} trip={trip} />
                </Box>
                <Box mt={4}>
                    <MapComponent tripItems={tripItems} />
                </Box>
            </Container>
        </Box>
    )
}
export default TripManaDetail;