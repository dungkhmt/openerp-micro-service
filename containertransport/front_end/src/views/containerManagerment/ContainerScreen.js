import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import { request } from "api";
import './styles.scss';
import HeaderContainerMana from "./HeaderContainerMana";
import ContentsContainerMana from "./ContentsContainerMana";
import { getContainers } from "api/ContainerAPI";

const ContainerScreen = () => {
    const [containers, setContainers] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    useEffect(() => {
        getContainers({page: page, pageSize: rowsPerPage})
        .then((res) => {
            console.log("container==========", res?.data.data.containerModels);
            setContainers(res?.data.data.containerModels);
        });
    }, [])

    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderContainerMana />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsContainerMana containers={containers} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>
    )
}
export default ContainerScreen;