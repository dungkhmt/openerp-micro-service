import { Box, Modal, Icon, Typography, Divider, TextField, Button } from "@mui/material";
import './styles.scss';
import PrimaryButton from "components/button/PrimaryButton";
import { menuIconMap } from "config/menuconfig";

const HeaderContainerMana = () => {
    return(
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Container Management</Typography>
            </Box>
            <Box className="btn-add"
            >
                <PrimaryButton className="btn-header">
                    <Icon className="icon">
                        {menuIconMap.get("ControlPointIcon")}
                    </Icon>
                    <Typography>
                        New Conatiner
                    </Typography>
                </PrimaryButton>
            </Box>
        </Box>
    )
}
export default HeaderContainerMana;