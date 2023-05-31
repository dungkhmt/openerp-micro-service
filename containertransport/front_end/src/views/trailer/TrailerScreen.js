import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import './styles.scss';
import HeaderTrailerScreen from "./HeaderTrailerScreen";
import { getTraler } from "api/TrailerAPI";
import ContentsTrailerScreen from "./ContentTrailerScreen";

const TrailerScreen = () => {
    const [trailer, setTrailer] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    useEffect(() => {
        getTraler({}).then((res) => {
            console.log("trailer", res?.data.data.trailerModels);
            setTrailer(res?.data.data.trailerModels);
            setCount(res?.data.data.count);
        })
    }, [page, rowsPerPage])
    

    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <HeaderTrailerScreen openModal={openModal} handleClose={handleClose} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTrailerScreen trailer={trailer} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}/>
            </Container>
        </Box>
    )
}
export default TrailerScreen;