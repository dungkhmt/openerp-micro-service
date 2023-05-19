import { Alert, Box, Container, Divider } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import './styles.scss';
import ContentsOrderManagerment from "./ContentsOrderManagerment";
import HeaderOrderScreen from "./HeaderOrderScreen";

const OrderScreen = () => {
    const [orders, setOrders] = useState([]);
    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();

    useEffect(() => {
        request(
            "post",
            `/order/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("order==========", res.data)
            setOrders(res.data.data);
        });
    }, [toastOpen])
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
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
                <ContentsOrderManagerment orders={orders} />
            </Container>
        </Box>
    );
}
export default OrderScreen;