import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import { request } from "api";
import './styles.scss';
import HeaderContainerMana from "./HeaderContainerMana";
import ContentsContainerMana from "./ContentsContainerMana";

const ContainerScreen = () => {
    const [containers, setContainers] = useState([]);

    useEffect(() => {
        request(
            "post",
            `/container/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("container==========", res.data)
            setContainers(res.data);
        });
    }, [])

    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderContainerMana />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsContainerMana containers={containers}/>
            </Container>
        </Box>
    )
}
export default ContainerScreen;