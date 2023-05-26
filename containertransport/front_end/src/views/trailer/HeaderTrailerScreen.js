import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
// import ModalTruck from "./ModalTruck";

const HeaderTrailerScreen = ({openModal, handleClose}) => {
    return (
        <Box className="headerTruckScreen">
            <Box className="title">
                <Typography >Trailer Management</Typography>
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
            {/* {openModal ? (<ModalTruck openModal={openModal} handleClose={handleClose} />) : null} */}
        </Box>
    );
};
export default HeaderTrailerScreen;