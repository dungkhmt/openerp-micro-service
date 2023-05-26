import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import './styles.scss';
import { request } from "api";
import HeaderTrailerScreen from "./HeaderTrailerScreen";
import { getTraler } from "api/TrailerAPI";
import ContentsTrailerScreen from "./ContentTrailerScreen";

const TrailerScreen = () => {
    const [trailer, setTrailer] = useState([]);
    const [openModal, setOpenModal] = useState(false);

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    useEffect(() => {
        getTraler({}).then((res) => {
            console.log("trailer", res.data.data);
            setTrailer(res.data.data);
        })
    }, [])
    

    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderTrailerScreen openModal={openModal} handleClose={handleClose} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTrailerScreen trailer={trailer} />
            </Container>
        </Box>
    )
}
export default TrailerScreen;