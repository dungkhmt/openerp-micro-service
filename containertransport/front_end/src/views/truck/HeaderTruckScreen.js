import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import ModalTruck from "./ModalTruck";

const HeaderTruckScreen = ({openModal, handleClose, setToast, setToastType, setToastMsg}) => {
    return (
        <Box className="headerTruckScreen">
            <Box className="title">
                <Typography >Trucks Management</Typography>
            </Box>
            <Box className="btn-add" onClick={handleClose}
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Truck
                    </Typography>
                </PrimaryButton>
            </Box>
            {openModal ? (<ModalTruck openModal={openModal} handleClose={handleClose} 
            setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
        </Box>
    );
};
export default HeaderTruckScreen;