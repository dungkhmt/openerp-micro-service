import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";

const HeaderFacilityScreen = () => {
    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Facility Management</Typography>
            </Box>
            <Box className="btn-add"
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Facility
                    </Typography>
                </PrimaryButton>
            </Box>
        </Box>
    );
}
export default HeaderFacilityScreen;