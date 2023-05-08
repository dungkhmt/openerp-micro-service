import React, {Fragment, useState} from "react";
import {DropzoneDialog} from "material-ui-dropzone";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import {Button} from "@mui/material";

function UploadButton({ buttonTitle, onClickSaveButton, filesLimit }) {
  const [open, setOpen] = useState(false);

  return (
    <Fragment>
      <Button
        color="primary"
        variant="contained"
        startIcon={<CloudUploadIcon />}
        onClick={() => setOpen(true)}
      >
        {buttonTitle}
      </Button>
      <DropzoneDialog
        acceptedFiles={[
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        ]}
        filesLimit={filesLimit}
        dropzoneText="Kéo và thả một tệp ở đây hoặc click để chọn tệp"
        dialogTitle="Tải lên danh sách lớp"
        useChipsForPreview={true}
        cancelButtonText="HUỶ"
        submitButtonText="LƯU"
        maxFileSize={5000000}
        open={open}
        onChange={(newFileObjs) => {
          console.log(
            "newFileObjs in method onChange, DropZoneDialog (UploadButton component)",
            newFileObjs
          );
        }}
        onClose={() => setOpen(false)}
        onSave={(files) => {
          setOpen(false);
          onClickSaveButton(files);
        }}
        showPreviews={true}
        showFileNamesInPreview={true}
      />
    </Fragment>
  );
}

export default UploadButton;
