import { Box, Container, Divider } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import './styles.scss';
import ContentsOrderManagerment from "./ContentsOrderManagerment";
import HeaderOrderScreen from "./HeaderOrderScreen";

const OrderScreen = () => {
    const [orders, setOrders] = useState([]);

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
                <HeaderOrderScreen />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsOrderManagerment orders={orders} />
            </Container>
        </Box>
    );
}
export default OrderScreen;