import React, { useContext, useEffect, useState } from "react";
import { Box, Container, Divider, Typography } from "@mui/material";
import ContentsFacilityMana from "./ContentFacillityMana";
import './styles.scss';
import HeaderFacilityScreen from "./HeaderFacilityScreen";
import { getFacility } from "api/FacilityAPI";
import { MyContext } from "contextAPI/MyContext";

const FacilityScreen = () => {
    const [facilities, setFacilities] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);
    const { role, preferred_username } = useContext(MyContext);

    useEffect(() => {
        getFacility({page: page, pageSize: rowsPerPage, owner: preferred_username})
        .then((res) => {
            console.log("facility==========", res?.data.data.facilityModels)
            setFacilities(res?.data.data.facilityModels);
            setCount(res.data.data.count);
          });
          console.log("role", role);
    }, [page, rowsPerPage])
    return(
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
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