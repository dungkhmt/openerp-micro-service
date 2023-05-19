import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { request } from "api";
import { useHistory, useLocation, useParams } from "react-router-dom";
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { MyContext } from "contextAPI/MyContext";
import TripsContents from "./TripsContents";

const TripsScreen = () => {
    const history = useHistory();
    const { shipmentId } = useParams();
    const [trips, setTrips] = useState([]);

    useEffect(() => {
        let data ={
            shipmentId: shipmentId
        }
        request(
            "post",
            `/trip/`, {},{}, data,{},
          ).then((res) => {
            setTrips(res.data.data);
          });
    }, [])
    console.log("=========", trips)
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <Box className="headerScreen-trip-detail">
                    <Box className="headerScreen-trip-detail-go-back"
                     onClick={() => history.push('/shipment')}
                     sx={{cursor: "pointer"}}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back shipment Screen</Typography>
                    </Box>
                    <Box className="headerScreen-trip-detail-info">
                        <Box className="title">
                            <Typography >Trips Managerment</Typography>
                        </Box>
                        <Box className="btn-add"
                            onClick={() => history.push('/shipment/trip/create')}
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
                        {/* <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                            onClick={handleCancelCreateShipment}
                        >Cancel</Button> */}
                        {/* <Button variant="contained" className="header-submit-shipment-btn-save"
                            onClick={handleSubmitShipment}
                        >Save</Button> */}
                    </Box>

                </Box>
                <Box className="divider">
                    <Divider />
                </Box>
                <TripsContents trips={trips} />
            </Container>
        </Box>
    )
}
export default TripsScreen;