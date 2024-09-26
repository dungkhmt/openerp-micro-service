import React, { useEffect, useState } from "react";
import { Alert, Box, Container, Divider, Icon, Typography } from "@mui/material";
import './../../styles.scss';
import { useHistory, useParams } from "react-router-dom";
import { getContainers } from "api/ContainerAPI";
import ContentsContainerMana from "views/containerManagerment/ContentsContainerMana";
import { menuIconMap } from "config/menuconfig";

const TypeContainerDetail = () => {
    const { typeId } = useParams();
    const history = useHistory();
    const [containers, setContainers] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [flag, setFlag] = useState(false);

    useEffect(() => {
        let data = {
            page: page,
            pageSize: rowsPerPage,
            containerSize: Number(typeId)
        }
        getContainers(data)
            .then((res) => {
                console.log("container==========", res?.data.data.containerModels);
                setContainers(res?.data.data.containerModels);
                setCount(res?.data.data.count);
            });
    }, [page, rowsPerPage, flag])
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >) : null}
                </Box>
                <Box className="header-detail">
                    <Box className="headerScreen-go-back"
                        onClick={() => {
                            history.push('/type/container')
                            // history.goBack()
                        }}
                        sx={{ cursor: "pointer" }}
                    >
                        <Icon>
                            {menuIconMap.get("ArrowBackIosIcon")}
                        </Icon>
                        <Typography>Go back type container screen</Typography>
                    </Box>
                    <Box className="headerScreen-detail-info">
                        <Box className="title-header">
                            <Typography >Containers Size {typeId}ft</Typography>
                        </Box>
                    </Box>
                </Box>
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsContainerMana containers={containers} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag} />
            </Container>
        </Box>
    )
}
export default TypeContainerDetail;