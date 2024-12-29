import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import FilePreviewUrl from "../../../common/uploader/FilePreviewUrl";
import {DialogActions} from "@mui/material";

function QuestionFilePreview(props) {

  const { open, setOpen, file} = props;

  const closeDialog = () => {
    setOpen(false)
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="lg">
        <DialogContent>
          <div>
            <FilePreviewUrl file={file}></FilePreviewUrl>
          </div>
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            onClick={closeDialog}
          >
            Hủy
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionFilePreview;
