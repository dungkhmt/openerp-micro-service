import { Alert, Box, Container, Divider } from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import './styles.scss';
import ContentsOrderManagerment from "./ContentsOrderManagerment";
import HeaderOrderScreen from "./HeaderOrderScreen";
import { getOrders } from "api/OrderAPI";
import { MyContext } from "contextAPI/MyContext";
import SearchBar from "components/search/SearchBar";

const OrderScreen = () => {
    const [orders, setOrders] = useState([]);

    const [toastOpen, setToast] = useState(false);
    const [toastType, setToastType] = useState();
    const [toastMsg, setToastMsg] = useState('');

    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [count, setCount] = useState(0);
    const { role, preferred_username } = useContext(MyContext);

    const [flag, setFlag] = useState(false);

    const [filters, setFilters] = useState([]);
    const status = [
        { name: "WAIT_APPROVE" },
        { name: "ORDERED" },
        { name: "DONE" },
        { name: "CANCEL" },
        { name: "SCHEDULED"},
        { name: "EXECUTING" }
    ]

    useEffect(() => {
        let data = { page: page, pageSize: rowsPerPage};
        let code = filters.find((item) => item.type === "code");
        if(code) {
            data.orderCode = code.value;
        }
        
        if(role?.includes("ADMIN")) {
            data.type = ["APPROVED"]
        }
        let status = filters.find((item) => item.type === "status");
        if(status) {
            let listStatus = [];
            listStatus.push(status.value);
            listStatus.push("APPROVED");
            data.status = listStatus;
        }
        getOrders(data).then((res) => {
            setOrders(res?.data?.data.orderModels);
            setCount(res?.data?.data.count);
        });
    }, [toastOpen, page, rowsPerPage, filters, flag])
    console.log("role", role);
    return (
        <Box className="fullScreen">
            <Container maxWidth="100vw" className="container">
                <Box className="toast">
                    {toastOpen ? (
                        <Alert variant="filled" severity={toastType} >
                            <strong>{toastMsg}</strong >
                        </Alert >
                        ) : null
                    }
                </Box>
                <HeaderOrderScreen setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />
                <Box className="divider">
                    <Divider />
                </Box>
                <Box>
                    <SearchBar filters={filters} setFilters={setFilters} status={status} />
                </Box>
                <ContentsOrderManagerment orders={orders} page={page} setPage={setPage}
                    rowsPerPage={rowsPerPage} setRowsPerPage={setRowsPerPage} count={count}
                    setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg}
                    flag={flag} setFlag={setFlag} />
            </Container>
        </Box>
    );
}
export default OrderScreen;