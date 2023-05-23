import { Box, Icon, Typography } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
import PrimaryButton from "components/button/PrimaryButton";
import './styles.scss';
import { useContext, useState } from "react";
import {useHistory } from 'react-router-dom';
import { request } from "api";
import { MyContext } from "contextAPI/MyContext";
import NewShipmentModal from "./NewShipmentModal";

const HeaderShipmentScreen = ({setToast, setToastType}) => {
    const history = useHistory();
    const [shipment, setShipment] = useState();
    const {preferred_username} = useContext(MyContext);
    const [open, setOpen] = useState(false);
    const submitShipment = () => {
        // history.push({
        //     pathname: '/shipment/create'
        // })
        setOpen(true);
    }

    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Shipment Management</Typography>
            </Box>
            <Box className="btn-add"
                onClick={submitShipment}
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
            {open ? (<NewShipmentModal open={open} setOpen={setOpen} setToast={setToast} setToastType={setToastType} />) : null}
        </Box>
    );
}
export default HeaderShipmentScreen;