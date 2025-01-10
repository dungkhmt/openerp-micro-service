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
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";

function QuestionFilePreview(props) {

  const { open, setOpen, file} = props;

  const closeDialog = () => {
    setOpen(false)
  }

  const handleDownload = () => {
    fetch(file)
      .then(response => response.blob())
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = getFilenameFromString(file); // Tên tệp tải xuống
        link.click();
        window.URL.revokeObjectURL(url); // Dọn dẹp URL blob
      })
      .catch(error => console.error('Error downloading the file:', error));
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
          <Button
            variant="contained"
            color="primary"
            onClick={handleDownload}
          >
            Tải xuống
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionFilePreview;
