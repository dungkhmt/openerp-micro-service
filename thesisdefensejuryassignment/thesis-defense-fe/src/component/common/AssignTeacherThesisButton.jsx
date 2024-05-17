import { Box } from "@material-ui/core/";
import PrimaryButton from "component/button/PrimaryButton";

export default function AssignTeacherThesisButton({ onClick }) {
  return (
    <Box display={"flex"} flexDirection={"row-reverse"} marginTop={3}>
      <PrimaryButton onClick={onClick}>
        Tạo hội đồng
      </PrimaryButton>
    </Box>
  );
}
