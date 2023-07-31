import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Icon, Typography } from "@mui/material";
import '../styles.scss';
import { useHistory, useParams } from "react-router-dom";
import { getTripByTripId } from "api/TripAPI";
import { menuIconMap } from "config/menuconfig";
import { getTripItemByTripId } from "api/TripItemAPI";
import TableOrder from "./TableOrder";
import MapComponent from "views/shipment/routing/Map";

const TripManaDetail = () => {
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [executed, setExecutes] = useState(false);
 
    const history = useHistory();

    const { tripId } = useParams();

    const [tripItems, setTripItems] = useState([]);
    const [trip, setTrip] = useState('');

    useEffect(() => {
        if (tripId) {
            getTripByTripId(tripId).then((res) => {
                setTrip(res?.data?.data)
            });
            getTripItemByTripId(tripId).then((res) => {
                setTripItems(res.data.data);
            })
        }
    }, [executed])

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
                        onClick={() => history.push('/trip/pending')}
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
                    </Box>

                </Box>

                <Box className="divider">
                    <Divider />
                </Box>

                <Box className="trip-items">
                    <TableOrder tripItems={tripItems} setExecutes={setExecutes} executed={executed} />
                </Box>
                <Box mt={4}>
                    <MapComponent tripItems={tripItems} />
                </Box>
            </Container>
        </Box>
    )
}
export default TripManaDetail;