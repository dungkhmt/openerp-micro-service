import { Typography } from "@mui/material";
import PrimaryButton from "../../../components/mui/button/PrimaryButton";
import TertiaryButton from "../../../components/mui/button/TertiaryButton";
import CustomizedDialogs from "../../../components/mui/dialog/CustomizedDialogs";
import PropTypes from "prop-types";

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

RejectFeedbackDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  handleContinue: PropTypes.func.isRequired,
  handleReject: PropTypes.func.isRequired,
};

export default RejectFeedbackDialog;
