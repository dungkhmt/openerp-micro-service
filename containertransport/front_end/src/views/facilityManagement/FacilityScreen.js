import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import ContentsFacilityMana from "./ContentFacillityMana";
import { request } from "api";
import './styles.scss';
import HeaderFacilityScreen from "./HeaderFacilityScreen";

const FacilityScreen = () => {
    const [facilities, setFacilities] = useState([]);
    useEffect(() => {
        request(
            "post",
            `/facility/`, {},{},{},{},
          ).then((res) => {
            console.log("facility==========", res.data)
            setFacilities(res.data.data);
          });
    }, [])
    return(
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderFacilityScreen />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsFacilityMana facilities={facilities}/>
            </Container>
        </Box>
    );
};
export default FacilityScreen;