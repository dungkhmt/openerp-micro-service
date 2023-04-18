import { Box, Container, Divider, Typography } from "@mui/material";
import HeaderTruckScreen from "./HeaderTruckScreen";
import './styles.scss';
import ContentsTruckManagement from "./ContentTruckManagement";

const TruckScreen = () => {
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderTruckScreen />
                <Box className="divider">
                    <Divider />
                </Box>
                <ContentsTruckManagement />
            </Container>
        </Box>
    );
};
export default TruckScreen;