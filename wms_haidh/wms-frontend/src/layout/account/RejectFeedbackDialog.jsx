import { Typography } from "@mui/material";
import PrimaryButton from "../../components/button/PrimaryButton";
import TertiaryButton from "../../components/button/TertiaryButton";
import CustomizedDialogs from "../../components/dialog/CustomizedDialogs";

const styles = { btn: { m: 0.5, width: 148 } };

function RejectFeedbackDialog({ open, handleContinue, handleReject }) {
  return (
    <CustomizedDialogs
      open={open}
      handleClose={handleContinue}
      contentTopDivider
      title="Bỏ phản hồi?"
      content={
        <Typography style={{ padding: "0 8px", width: 550, marginTop: -1 }}>
          Nếu bỏ bây giờ, bạn sẽ không chia sẻ bất cứ ý kiến đóng góp nào với
          chúng tôi.
        </Typography>
      }
      actions={
        <>
          <TertiaryButton sx={styles.btn} onClick={handleContinue}>
            Tiếp tục chỉnh sửa
          </TertiaryButton>
          <PrimaryButton
            sx={styles.btn}
            onClick={handleReject}
            style={{ marginLeft: 4 }}
          >
            Bỏ
          </PrimaryButton>
        </>
      }
    />
  );
}

export default RejectFeedbackDialog;
