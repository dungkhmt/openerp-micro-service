import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { useHistory, useLocation, useParams } from "react-router-dom";
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { MyContext } from "contextAPI/MyContext";
import TripsContents from "./TripsContents";
import { getShipmentByCode } from "api/ShipmentAPI";
import ShipmentContents from "./ShipmentContents";
import { getTrips } from "api/TripAPI";

const ShipmentDetail = () => {
    const history = useHistory();
    const { shipmentId } = useParams();
    const [shipment, setShipment] = useState();
    const [trips, setTrips] = useState([]);

    useEffect(() => {
        let data = {
            shipmentId: shipmentId
        }
        getTrips(data).then((res) => {
            setTrips(res.data.data);
        });

        getShipmentByCode(shipmentId).then((res) => {
            setShipment(res.data.data);
        });


    }, [])
    console.log("=========", shipment)
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
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
                            // onClick={handleSubmitShipment}
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
                {trips.length > 0 ? <TripsContents trips={trips} shipmentId={shipmentId} /> : null}
            </Container>
        </Box>
    )
}
export default ShipmentDetail;