import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import ModalTrailer from "./modal/ModalTrailer";

const HeaderTrailerScreen = ({openModal, handleClose, setToast, setToastType, setToastMsg}) => {
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
                        New Trailer
                    </Typography>
                </PrimaryButton>
            </Box>
            {openModal ? (<ModalTrailer open={openModal} handleClose={handleClose}
            setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
        </Box>
    );
};
export default HeaderTrailerScreen;