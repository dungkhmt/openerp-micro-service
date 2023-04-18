import { Box, Modal, Icon, Typography, Divider, TextField } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";
import { useState } from "react";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 700,
    bgcolor: 'background.paper',
    p: 4,
  };

const HeaderTruckScreen = () => {
    const [openModal, setOpenModal] = useState(false);

    const handleClose = () => {
        setOpenModal(!openModal);
    }
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

            <Modal
                open={openModal}
                onClose={handleClose}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box className="modal">
                    <Box className="header-modal">
                        <Typography>New Truck</Typography>
                    </Box>
                    <Divider />
                    <Box className="body-modal">
                        <Box >
                            <Box>
                                <Typography>Facility</Typography>
                            </Box>
                            <Box>
                                <TextField id="outlined-basic" label="Outlined" variant="outlined" />
                            </Box>
                        </Box>
                    </Box>
                    <Divider />
                    <Box className="footer-modal">
                        <Box></Box>
                        <Box>
                            <Box></Box>
                            <Box></Box>
                        </Box>
                    </Box>
                </Box>
            </Modal>
        </Box>
    );
};
export default HeaderTruckScreen;