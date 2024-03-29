import { Box, Grid, TextField, FormControl, Typography, Button, IconButton, Modal, OutlinedInput, InputAdornment } from "@mui/material";
import { request } from "api";
import MapComponent from "components/map";
import React, { useEffect, useState, useRef } from "react";
import { StandardTable } from "erp-hust/lib/StandardTable";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';

import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import dayjs from "dayjs";


const OrderScreen = () => {

    const [isCustomer, setIsCustomer] = useState(false)

    const [listOrder, setListOrder] = useState([])


    const mapRef = useRef();

    const [formDataOrder, setFormDataOrder] = useState({
        weight: 0,
        volume: 0,
        fromDateTime: "",
        toDateTime: "",
        address: "",
        latitude: "",
        longitude: "",
        status: "PENDING",

    });
    const [formDataCreateCustomer, setFormDataCreateCustomer] = useState({
        customerName: "",
        phone: "",
        address: "",
        status: "",
        latitude: "",
        longitude: ""
    });
    const [selectedDateRange, setSelectedDateRange] = useState([dayjs('2024-04-01'), null]);

    const [orderEdit, setOrderEdit] = useState(null);
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => {
        setFormDataOrder({
            weight: 0,
            volume: 0,
            fromDateTime: "",
            toDateTime: "",
            address: "",
            latitude: "",
            longitude: "",
            status: "PENDING",
        })
        setOrderEdit(null);
        setOpen(false)
    }

    useEffect(() => {
        request("get", "/order/get-all-by-customer", (res) => {
            setListOrder(res.data)
        }).then()
    }, [])

    useEffect(() => {
        request("post", "/customer", (res) => {
            console.log(res.data)
            setIsCustomer(res.data)
        })
    }, [])


    const columns = [
        {
            title: "Weight",
            field: "weight",
        },
        {
            title: "Volume",
            field: "volume",
        },
        {
            title: "Address",
            field: "address",
        },
        {
            title: "From Date",
            field: "fromDateTime",
        },
        {
            title: "To Date",
            field: "toDateTime",
        },

        {
            title: "Edit",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleEditOrder(rowData)
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon />
                </IconButton>
            ),
        },
        {
            title: "Delete",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        handleDeleteOrder(rowData)
                    }}
                    variant="contained"
                    color="error"
                >
                    <DeleteIcon />
                </IconButton>
            ),
        },
    ];


    const handleEditOrder = (order) => {

        setOrderEdit(order)
        setFormDataOrder(order)
        setOpen(true)

    }

    const handleDeleteOrder = (order) => {

    }

    const handleAddOrder = () => {
        setOrderEdit(null)
        setOpen(true)
    }

    const handleChangeCustomerForm = (event) => {
        setFormDataCreateCustomer({
            ...formDataCreateCustomer,
            [event.target.name]: event.target.value
        });
    };

    const handleChangeOrderForm = (event) => {
        setFormDataOrder({
            ...formDataOrder,
            [event.target.name]: event.target.value
        });
    };
    const handleSubmit = (event) => {
        event.preventDefault();

        request("post", "/customer/register", (res) => {
            setIsCustomer(true)
        }, {}, formDataCreateCustomer).then()





    };
    const handleSubmitOrder = (event) => {
        event.preventDefault();
        console.log(formDataOrder)
        if (orderEdit) {
            request("put", `/order/${orderEdit.id}`, (res) => {
                setListOrder(listOrder.map((order) => {
                    if (order.id === orderEdit.id) {
                        return res.data
                    }
                    return order
                }))
            }, {}, formDataOrder).then()
        } else {
            request("post", "/order", (res) => {
                setListOrder([...listOrder, res.data])
            }, {}, formDataOrder).then()
        }
        handleClose()
    }


    return (
        <>
            <div>
                {!isCustomer ? (<Box
                    sx={{
                        top: '50%',
                        left: '50%',
                        // transform: 'translate(-50%, -50%)',
                        // width: 1000,
                        bgcolor: 'background.paper',
                        border: '2px solid #000',
                        boxShadow: 24,
                        p: 4,
                    }}
                >
                    <Grid container spacing={2}>
                        <Grid item xs={4}>
                            <form onSubmit={handleSubmit}>
                                <FormControl sx={{ width: '300px' }}>
                                    <Typography variant="h6" component="h2" gutterBottom>
                                        Enter Customer Information
                                    </Typography>
                                    <TextField
                                        fullWidth
                                        label="Name"
                                        name="customerName"
                                        value={formDataCreateCustomer.customerName}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Address"
                                        name="address"
                                        value={formDataCreateCustomer.address}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Phone"
                                        name="phone"
                                        value={formDataCreateCustomer.phone}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                    // required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Status"
                                        name="status"
                                        value={formDataCreateCustomer.status}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                    // required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Latitude"
                                        name="latitude"
                                        value={formDataCreateCustomer.latitude}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                        required
                                    />
                                    <TextField
                                        fullWidth
                                        label="Longitude"
                                        name="longitude"
                                        value={formDataCreateCustomer.longitude}
                                        onChange={handleChangeCustomerForm}
                                        margin="normal"
                                        required
                                    />


                                    <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                                        Submit
                                    </Button>
                                </FormControl>
                            </form>
                        </Grid>
                        <Grid item xs={8}>
                            <MapComponent isEdit mapRef={mapRef} setCoordinates={(lat, lng) => setFormDataCreateCustomer({ ...formDataCreateCustomer, latitude: lat, longitude: lng })} />
                        </Grid>

                    </Grid>

                </Box>) :
                    (
                        <div>
                            <div>
                                <h1>Quản lý đơn hàng</h1>
                                <Button onClick={() => {
                                    handleAddOrder()
                                }} type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                                    Thêm
                                </Button>
                            </div>
                            <StandardTable
                                title="Danh sách đơn hàng"
                                commandBarComponents={MyComponent}

                                columns={columns}
                                data={listOrder}
                                hideCommandBar
                                options={{
                                    selection: false,
                                    pageSize: 20,
                                    search: true,
                                    sorting: true,
                                }}
                            />
                        </div>

                    )

                }

            </div>
            <Modal
                open={open}
                onClose={handleClose}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box
                    sx={{
                        position: 'absolute',
                        top: '50%',
                        left: '50%',
                        width: 400,
                        transform: 'translate(-50%, -50%)',


                        bgcolor: 'background.paper',
                        border: '2px solid #000',
                        boxShadow: 24,
                        p: 4,
                    }}
                >

                    <form onSubmit={handleSubmitOrder}>
                        <FormControl sx={{ width: '300px' }}>
                            <Typography variant="h6" component="h2" gutterBottom>
                                Enter Order Information
                            </Typography>
                            <TextField
                                fullWidth
                                label="Weight"
                                name="weight"
                                value={formDataOrder.weight}
                                onChange={handleChangeOrderForm}
                                InputProps={{
                                    endAdornment: <InputAdornment position="start">kg</InputAdornment>,
                                }}
                                margin="normal"
                                required
                            />

                            <TextField
                                fullWidth
                                label="Volume"
                                name="volume"
                                value={formDataOrder.volume}
                                onChange={handleChangeOrderForm}
                                InputProps={{
                                    endAdornment: <InputAdornment position="start">lit</InputAdornment>,
                                }}
                                margin="normal"
                                required
                            />
                            <TextField
                                fullWidth
                                label="Address"
                                name="address"
                                value={formDataOrder.address}
                                onChange={handleChangeOrderForm}
                                margin="normal"
                                required
                            />
                            <LocalizationProvider dateAdapter={AdapterDayjs}>

                                <DateTimePicker
                                    renderInput={(props) => <TextField {...props} name="fromDateTime"
                                        value={formDataOrder.fromDateTime} required />}
                                    label="From Date"
                                    name="fromDateTime"

                                    onChange={(newValue) => {
                                        setFormDataOrder({
                                            ...formDataOrder,
                                            fromDateTime: newValue
                                        })
                                    }}
                                    required
                                />
                                <DateTimePicker
                                    renderInput={(props) =>
                                        <TextField
                                            {...props} name="toDateTime"
                                            value={formDataOrder.toDateTime}
                                            required
                                        />}
                                    name="toDateTime"
                                    label="To Date"
                                    onChange={(newValue) => {
                                        setFormDataOrder({
                                            ...formDataOrder,
                                            toDateTime: newValue
                                        })
                                    }
                                    }
                                    required
                                />

                            </LocalizationProvider>



                            <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                                Submit
                            </Button>
                        </FormControl>
                    </form>


                </Box>
            </Modal>
        </>
    )
};

export default OrderScreen;

const MyComponent = () => {
    return (
        <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
            Thêm
        </Button>
    )
}