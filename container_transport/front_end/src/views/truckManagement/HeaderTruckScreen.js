import { Box, Container, Icon, Typography } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";

const HeaderTruckScreen = () => {
    return (
        <Box className="headerTruckScreen">
            <Box className="title">
                <Typography >Trucks Management</Typography>
            </Box>
            <PrimaryButton className="btn-header">
                <Icon className="icon">
                    {menuIconMap.get("ControlPointIcon")}
                </Icon>
                <Typography>
                    New Truck
                </Typography>
            </PrimaryButton>

        </Box>
    );
};
export default HeaderTruckScreen;