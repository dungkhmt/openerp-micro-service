import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { useState } from "react";
import FacilityModal from "./create/NewFacilityModal";

const HeaderFacilityScreen = ({openModal, handleClose, setToast, setToastType, setToastMsg}) => {
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
            onClick={handleClose}
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
            {openModal ? (<FacilityModal open={openModal} handleClose={handleClose}
            setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
        </Box>
    );
}
export default HeaderFacilityScreen;