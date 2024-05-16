import { Box } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";

export default function AssignTeacherThesisButton({ onClick }) {
    return (
        <Box display={"flex"} flexDirection={"row-reverse"} marginTop={3}>
            <PrimaryButton onClick={onClick}>
                Tạo hội đồng
            </PrimaryButton>
        </Box>
    );
}
