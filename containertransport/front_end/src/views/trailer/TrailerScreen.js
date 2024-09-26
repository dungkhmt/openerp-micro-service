import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Typography } from "@mui/material";
import './styles.scss';
import HeaderTrailerScreen from "./HeaderTrailerScreen";
import { getTraler } from "api/TrailerAPI";
import ContentsTrailerScreen from "./ContentTrailerScreen";
import SearchBar from "components/search/SearchBar";

const TrailerScreen = () => {
    const [trailer, setTrailer] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [openModal, setOpenModal] = useState(false);

    const [flag, setFlag] = useState(false); 

    const [filters, setFilters] = useState([]);

    const status = [
        { name: "AVAILABLE" },
        { name: "SCHEDULED" },
        { name: "EXECUTING" }
    ]

    const handleClose = () => {
        setOpenModal(!openModal);
    }
    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.trailerCode = code.value;
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
        }
        getTraler(data).then((res) => {
            console.log("trailer", res?.data.data.trailerModels);
            setTrailer(res?.data.data.trailerModels.sort((a, b) => a.updateAt - b.updateAt));
            setCount(res?.data.data.count);
        })
    }, [openModal, page, rowsPerPage, flag])

    useEffect(() => {
        let data = { 
            page: page,
            pageSize: rowsPerPage,
        }
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.trailerCode = code.value;
            data.page = 0;
            setPage(0);
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            data.status = status.value;
            data.page = 0;
            setPage(0);
        }
        getTraler(data).then((res) => {
            console.log("trailer", res?.data.data.trailerModels);
            setTrailer(res?.data.data.trailerModels.sort((a, b) => a.updateAt - b.updateAt));
            setCount(res?.data.data.count);
        })
    }, [filters])


    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <HeaderTrailerScreen openModal={openModal} handleClose={handleClose}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <Box>
                    <SearchBar filters={filters} setFilters={setFilters} status={status} type="status" />
                </Box>
                <ContentsTrailerScreen trailer={trailer} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag} />
            </Container>
        </Box>
    )
}
export default TrailerScreen;