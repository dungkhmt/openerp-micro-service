import { Alert, Box, Container, Divider } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import '../styles.scss';
import { getOrders } from "api/OrderAPI";
import { MyContext } from "contextAPI/MyContext";
import HeaderOrderScreen from "../HeaderOrderScreen";
import ContentsOrderManagerment from "../ContentsOrderManagerment";

const OrderWaitApprove = () => {
    const [orders, setOrders] = useState([]);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);
    const { role, preferred_username } = useContext(MyContext);

    useEffect(() => {
        let data = {
            page: page, 
            pageSize: rowsPerPage,
            status: "WAIT_APPROVE"
        }
        getOrders(data).then((res) => {
            setOrders(res?.data.data.orderModels);
            setCount(res?.data.data.count);
        });
    }, [toastOpen, page, rowsPerPage])
    return (
        <Box className="fullScreen">
            <Container maxWidth="lg" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >
                        ) : null
                    }
                </Box>
                <HeaderOrderScreen setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} type="WaitApprove" />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsOrderManagerment orders={orders} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count} type="WaitApprove" />
            </Container>
        </Box>
    );
}
export default OrderWaitApprove;