import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import '../styles.scss';
import { getTripByDriver } from "api/TripAPI";
import TableTrip from "../component/TableTrip";

const TripExecutedScreen = () => {

    const [trips, setTrips] = useState([]);
    useEffect(() => {
        let data = {
            status: "DONE"
        };
        getTripByDriver(data).then((res) => {
            console.log("=========", res?.data.data);
            setTrips(res?.data?.data);
        })
    }, [])
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="trip-header">
                    <Box className="title">
                        <Typography>Trips Executed Management</Typography>
                    </Box>
                </Box>

                <Box className="divider">
                    <Divider />
                </Box>

                <Box className="list-trip">
                    <TableTrip trips={trips} type="Done" />
                </Box>
            </Container>
        </Box>
    )
}
export default TripExecutedScreen;