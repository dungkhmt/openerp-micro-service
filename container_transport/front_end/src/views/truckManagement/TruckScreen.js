import { Box, Container, Typography } from "@mui/material";
import HeaderTruckScreen from "./HeaderTruckScreen";
import './styles.scss';
import ContentsTruckManagement from "./ContentTruckManagement";

const TruckScreen = () => {
    return (
        <Box className="fullScreen">
            <Container maxWidth="md" className="container">
                <HeaderTruckScreen />
                <ContentsTruckManagement />
            </Container>
        </Box>
    );
};
export default TruckScreen;