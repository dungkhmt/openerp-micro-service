import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { useState } from "react";
import FacilityModal from "./create/NewFacilityModal";

const HeaderFacilityScreen = () => {
    const [open, setOpen] = useState(false);
    const handleNewFacility = () => {
        setOpen(true);
    }

    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Facility Management</Typography>
            </Box>
            <Box className="btn-add"
            onClick={handleNewFacility}
            >
                <PrimaryButton className="btn-headerScreen">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Facility
                    </Typography>
                </PrimaryButton>
            </Box>
            {open ? (<FacilityModal open={open} setOpen={setOpen} />) : null}
        </Box>
    );
}
export default HeaderFacilityScreen;