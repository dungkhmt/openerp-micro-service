import { Alert, AlertTitle, Box, Container, Divider } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import './styles.scss';
import ContentsOrderManagerment from "./ContentsOrderManagerment";
import HeaderOrderScreen from "./HeaderOrderScreen";

const OrderScreen = () => {
    const [orders, setOrders] = useState([]);
    const [toastOpen, setToast] = useState(false);

    useEffect(() => {
        request(
            "post",
            `/order/`, {}, {}, {}, {},
        ).then((res) => {
            console.log("order==========", res.data)
            setOrders(res.data.data);
        });
    }, [])
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert severity="success" >
                            <AlertTitle>Success</AlertTitle>
                            This is a success alert — <strong> check it out!</strong >
                        </Alert >
                    ) : null
                    }
                </Box>
                <HeaderOrderScreen setToast={setToast} />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsOrderManagerment orders={orders} />
            </Container>
        </Box>
    );
}
export default OrderScreen;