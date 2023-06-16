import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { useState } from "react";
import ModalContainer from "./modal/ModalContainer";

const HeaderContainerMana = () => {
    const [open, setOpen] = useState(false);

    const handleClose = () => {
        setOpen(!open)
    }
    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Container Management</Typography>
            </Box>
            <Box className="btn-add"
                onClick={() => handleClose()}
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Container
                    </Typography>
                </PrimaryButton>
            </Box>
            {open ? (<ModalContainer open={open} handleClose={handleClose} />) : null}
        </Box>
    )
}
export default HeaderContainerMana;