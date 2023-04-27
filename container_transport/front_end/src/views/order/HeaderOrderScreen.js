import { Box, Icon, Typography } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
import PrimaryButton from "components/button/PrimaryButton";
import './styles.scss';
import NewOrderModal from "./NewOrderModal";
import { useState } from "react";

const HeaderOrderScreen = ({setToast, setToastType}) => {
    const [open, setOpen] = useState(false);
    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Order Management</Typography>
            </Box>
            <Box className="btn-add"
                onClick={() => setOpen(true)}
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Order
                    </Typography>
                </PrimaryButton>
            </Box>
            {open ? (<NewOrderModal open={open} setOpen={setOpen} setToast={setToast} setToastType={setToastType} />) : null}
        </Box>
    );
}
export default HeaderOrderScreen;