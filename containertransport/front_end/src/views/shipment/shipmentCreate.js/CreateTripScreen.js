import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { Box, Button, Container, Divider, Icon, Typography } from "@mui/material";
import { request } from "api";
import { useHistory, useLocation } from "react-router-dom";
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { MyContext } from "contextAPI/MyContext";
import CreateTripContents from "./CreateTripContents";
// import CreateTripContents from "./CreateTripContents";

const TripScreen = () => {
    const history = useHistory();
    const location = useLocation();
    // const [shipmentId, setShipmentId] = useState(location.state.shipmentId);
    const { tripsCreate, setTripCreate } = useContext(MyContext);
    const { truckScheduler, setTruckScheduler } = useContext(MyContext);
    const { ordersScheduler, setOrderScheduler, preferred_username } = useContext(MyContext);

    useEffect(() => {
        let body = {
            shipmentId: 5
        };
    }, [])
    const handleCancelCreateShipment = () => {
        setTripCreate([]);
        setTruckScheduler([]);
        setOrderScheduler([]);
        history.push('/shipment');
    }
    const handleSubmitShipment = () => {
        let tripListTmp = [];
        tripsCreate.forEach((item) => {
            let tripListItem = {
                truckId: item.truck.id,
                created_by_user_id: item.created_by_user_id,
                orderIds: item.orderIds,
                tripItemModelList: item.tripItemModelList
            }
            tripListTmp.push(tripListItem);
        })
        let data = {
            created_by_user_id: preferred_username,
            tripList: tripListTmp
        }
        console.log("data", data);
        request(
            "post",
            `/shipment/create`, {}, {}, data
        ).then((res) => {
            console.log(res);
            setTripCreate([]);
            setTruckScheduler([]);
            setOrderScheduler([]);
            history.push('/shipment');
        })
    }
    console.log("tripsCreate", tripsCreate)
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <Box className="headerScreen">
                    <Box className="title">
                        <Typography >Create Shipment</Typography>
                    </Box>
                    <Box className="btn-header">
                        <Button variant="outlined" color="error" className="header-create-shipment-btn-cancel"
                            onClick={handleCancelCreateShipment}
                        >Cancel</Button>
                        <Button variant="contained" className="header-submit-shipment-btn-save"
                            onClick={handleSubmitShipment}
                        >Save</Button>
                    </Box>

                </Box>
                <Box className="divider">
                    <Divider />
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
                <CreateTripContents trips={tripsCreate} />
            </Container>
        </Box>
    )
}
export default TripScreen;