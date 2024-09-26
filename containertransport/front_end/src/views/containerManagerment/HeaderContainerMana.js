import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap, roles } from "config/menuconfig";
import { useContext, useState } from "react";
import ModalContainer from "./modal/ModalContainer";
import { MyContext } from "contextAPI/MyContext";

const HeaderContainerMana = ({ openModal, handleClose, setToast, setToastType, setToastMsg }) => {
    const { role, preferred_username } = useContext(MyContext);
    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Container Management</Typography>
            </Box>
            {role.includes(roles.get("Customs")) || role.includes(roles.get("Admin")) ? (
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
            ) : null}

            {openModal ? (<ModalContainer open={openModal} handleClose={handleClose}
                setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
        </Box>
    )
}
export default HeaderContainerMana;