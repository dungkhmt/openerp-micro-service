import { Alert, Box, Container, Divider } from "@mui/material";
import React, { useEffect, useState } from "react";
import './styles.scss';
import ContentsOrderManagerment from "./ContentsOrderManagerment";
import HeaderOrderScreen from "./HeaderOrderScreen";
import { getOrders } from "api/OrderAPI";

const OrderScreen = () => {
    const [orders, setOrders] = useState([]);
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);

    useEffect(() => {
        getOrders({page: page, pageSize: rowsPerPage}).then((res) => {
            setOrders(res.data.data.orderModels);
            setCount(res.data.data.count);
        });
    }, [toastOpen, page, rowsPerPage])
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        toastType === "success" ? (
                            <Alert variant="filled" severity={toastType} >
                                <strong> Created Order Success !!!</strong >
                            </Alert >
                        ) : (
                            <Alert variant="filled" severity={toastType} >
                                <strong> Created Order False !!!</strong >
                            </Alert >
                        )) : null
                    }
                </Box>
                <HeaderOrderScreen setToast={setToast} setToastType={setToastType} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsOrderManagerment orders={orders} page={page} setPage={setPage}
                rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}/>
            </Container>
        </Box>
    );
}
export default OrderScreen;