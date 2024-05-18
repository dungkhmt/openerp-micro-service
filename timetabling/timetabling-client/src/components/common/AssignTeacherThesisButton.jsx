import { Box } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";

export default function AssignTeacherThesisButton({ onClick, children }) {
    return (
        <Box display={"flex"} flexDirection={"row-reverse"} marginTop={3}>
            <PrimaryButton onClick={onClick}>
                {children}
            </PrimaryButton>
        </Box>
    );
}
