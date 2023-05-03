import { Autocomplete, Box, TextField, Typography } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import './styles.scss';

const ChoseTruckAndOrders = ({ trucks, setTruckId }) => {
    return (
        <>
            <Box className="chose-truck">
                <Typography className="chose-truck-text">Truck:</Typography>
                <Autocomplete
                    disablePortal
                    id="combo-box-demo"
                    options={trucks}
                    getOptionLabel={(option) => option.truckCode}
                    // sx={{ width: 300 }}
                    renderInput={(params) => <TextField {...params} label="chose truck" />}
                />
            </Box>
            <Box className="chose-orders">
                <Typography className="chose-order-text">Orders:</Typography>
            </Box>
        </>
    )
}
export default ChoseTruckAndOrders;