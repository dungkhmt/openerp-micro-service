import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import HeaderTruckScreen from "./HeaderTruckScreen";
import './styles.scss';
import ContentsTruckManagement from "./ContentTruckManagement";
import { request } from "api";

const TruckScreen = () => {

    const [trucks, setTrucks] = useState([]);
    const [openModal, setOpenModal] = useState(false);

    const handleClose = () => {
        setOpenModal(!openModal);
    }

    useEffect(() => {
        request(
          "post",
          `/truck/`, {},{},{},{},
        ).then((res) => {
          console.log("truck==========", res.data)
          setTrucks(res.data);
        });
      }, [openModal]);
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderTruckScreen openModal={openModal} handleClose={handleClose} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTruckManagement trucks={trucks}/>
            </Container>
        </Box>
    );
};
export default TruckScreen;