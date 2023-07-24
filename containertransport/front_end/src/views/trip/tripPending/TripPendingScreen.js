import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import '../styles.scss';
import { getTripByDriver } from "api/TripAPI";
import TableTrip from "../component/TableTrip";

const TripPendingScreen = () => {
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [trips, setTrips] = useState([]);
    const [executed, setExecutes] = useState(false)

    useEffect(() => {
        let data = {
            status: "Pending"
        };
        getTripByDriver(data).then((res) => {
            console.log("=========", res.data.data);
            setTrips(res?.data?.data);
        })
    }, [executed])

    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>

                <Box className="trip-header">
                    <Box className="title">
                        <Typography>Trips Pending Management</Typography>
                    </Box>
                </Box>

                <Box className="divider">
                    <Divider />
                </Box>

                <Box className="list-trip">
                    <TableTrip trips={trips} setExecutes={setExecutes} executed={executed} />
                </Box>
            </Container>
        </Box>
    )
}
export default TripPendingScreen;