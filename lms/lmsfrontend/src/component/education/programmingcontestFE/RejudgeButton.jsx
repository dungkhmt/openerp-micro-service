import ReplayIcon from "@mui/icons-material/Replay";
import { IconButton } from "@mui/material";
import { request } from "api";
import { infoNoti } from "utils/notification";

export const RejudgeButton = ({ submissionId }) => {
  const handleRejudge = (submissionId) => {
    request("post", "/submissions/" + submissionId + "/evaluation", (res) => {
      infoNoti(
        `Rejudging the submission with id ${submissionId.substring(0, 6)}`
      );
    });
  };

  return (
    <IconButton
      variant="contained"
      color="primary"
      onClick={() => {
        handleRejudge(submissionId);
      }}
    >
      <ReplayIcon />
    </IconButton>
  );
};
