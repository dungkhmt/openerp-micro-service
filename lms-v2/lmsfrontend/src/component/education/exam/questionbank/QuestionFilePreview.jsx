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
          <div>
            <Button
              variant="contained"
              onClick={closeDialog}
            >
              Hủy
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionFilePreview;
