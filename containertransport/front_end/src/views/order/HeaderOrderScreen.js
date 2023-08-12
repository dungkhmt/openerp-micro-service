import { Box, Icon, Typography } from "@mui/material";
import { menuIconMap, roles } from "config/menuconfig";
import PrimaryButton from "components/button/PrimaryButton";
import './styles.scss';
import NewOrderModal from "./NewOrderModal";
import { useContext, useState } from "react";
import { MyContext } from "contextAPI/MyContext";

const HeaderOrderScreen = ({ setToast, setToastType, setToastMsg }) => {
    const [open, setOpen] = useState(false);
    const { role, preferred_username } = useContext(MyContext);
    return (
        <Box className="headerScreen">
            <Box className="title">
                <Typography >Order Management</Typography>
            </Box>
            {role.includes(roles.get("Customer")) ? (
                <Box className="btn-add"
                    onClick={() => setOpen(true)}
                >
                    <PrimaryButton className="btn-header">
                        <Icon className="icon">
                            {menuIconMap.get("ControlPointIcon")}
                        </Icon>
                        <Typography>
                            New Order
                        </Typography>
                    </PrimaryButton>
                </Box>
            ) : null}

            {open ? (<NewOrderModal open={open} setOpen={setOpen} setToast={setToast} setToastType={setToastType} setToastMsg={setToastMsg} />) : null}
        </Box>
    );
}
export default HeaderOrderScreen;