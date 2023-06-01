import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import HeaderTruckScreen from "./HeaderTruckScreen";
import './styles.scss';
import ContentsTruckManagement from "./ContentTruckManagement";
import { getTrucks } from "api/TruckAPI";

const TruckScreen = () => {

    const [trucks, setTrucks] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const handleClose = () => {
        setOpenModal(!openModal);
    }

    useEffect(() => {
        getTrucks({page: page, pageSize: rowsPerPage}).then((res) => {
          console.log("truck==========", res.data.truckModels)
          setTrucks(res.data.truckModels);
          setCount(res?.data.count);
        });
      }, [openModal, page, rowsPerPage]);
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
            <Box className="toast">
                    {toastOpen ? (
                    <Alert variant="filled" severity={toastType} >
                        <strong>{toastMsg}</strong >
                    </Alert > ) : null}
                </Box>
                <HeaderTruckScreen openModal={openModal} handleClose={handleClose}
                setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTruckManagement trucks={trucks} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} />
            </Container>
        </Box>
    );
};
export default TruckScreen;