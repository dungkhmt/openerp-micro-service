import { Box, Icon, Typography } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
import PrimaryButton from "components/button/PrimaryButton";
import './styles.scss';
import { useState } from "react";
import {useHistory } from 'react-router-dom';

const HeaderShipmentScreen = () => {
    const history = useHistory();

    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Shipment Management</Typography>
            </Box>
            <Box className="btn-add"
                onClick={() => history.push('/shipment/create')}
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Shipment
                    </Typography>
                </PrimaryButton>
            </Box>
            {/* {open ? (<NewOrderModal open={open} setOpen={setOpen} setToast={setToast} setToastType={setToastType} />) : null} */}
        </Box>
    );
}
export default HeaderShipmentScreen;