import { Box } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";
/**
 * Button to submit teacher and thesis to jury
 */

export default function AssignTeacherThesisButton({ onClick, children }) {
    return (
        // <Box display={"flex"} flexDirection={"row-reverse"}>
        <PrimaryButton onClick={onClick}>
            {children}
        </PrimaryButton>
        // </Box>
    );
}
