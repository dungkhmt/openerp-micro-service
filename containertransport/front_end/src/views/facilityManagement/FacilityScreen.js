import React, { useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import ContentsFacilityMana from "./ContentFacillityMana";
import './styles.scss';
import HeaderFacilityScreen from "./HeaderFacilityScreen";
import { getFacility } from "api/FacilityAPI";

const FacilityScreen = () => {
    const [facilities, setFacilities] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    useEffect(() => {
        getFacility({page: page, pageSize: rowsPerPage})
        .then((res) => {
            console.log("facility==========", res?.data.data.facilityModels)
            setFacilities(res?.data.data.facilityModels);
            setCount(res.data.data.count);
          });
    }, [page, rowsPerPage])
    return(
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderFacilityScreen />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsFacilityMana facilities={facilities} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}/>
            </Container>
        </Box>
    );
};
export default FacilityScreen;