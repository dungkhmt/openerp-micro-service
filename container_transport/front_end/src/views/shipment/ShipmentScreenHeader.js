import { Box, Icon, Typography } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
import PrimaryButton from "components/button/PrimaryButton";
import './styles.scss';
import { useContext, useState } from "react";
import {useHistory } from 'react-router-dom';
import { request } from "api";
import { MyContext } from "contextAPI/MyContext";

const HeaderShipmentScreen = () => {
    const history = useHistory();
    const [shipment, setShipment] = useState();
    const {preferred_username} = useContext(MyContext);
    const submitShipment = () => {
        // const data = {
        //     created_by_user_id: preferred_username
        // }
        // request(
        //     "post",
        //     `/shipment/create`, {}, {}, data
        // ).then((res) => {
        //     console.log("shipment", res.data.data);
        // })
        history.push({
            pathname: '/shipment/create'
        })
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
            {/* {open ? (<NewOrderModal open={open} setOpen={setOpen} setToast={setToast} setToastType={setToastType} />) : null} */}
        </Box>
    );
}
export default HeaderShipmentScreen;