import React from "react";
import ContestManagerViewSubmissionOfAUser from "./ContestManagerViewSubmissionOfAUser";
import HustModal from "../../common/HustModal";

export default function ContestManagerViewSubmissionOfAUserDialog(props) {
  const {contestId, userId, open, onClose} = props;



  return (
    <HustModal
      title="Submissions"
      open={open}
      onClose={onClose}
      maxWidthPaper={1200}
      isNotShowCloseButton
    >
      <ContestManagerViewSubmissionOfAUser
        contestId={contestId}
        userId={userId}
      />
    </HustModal>
  );
}
