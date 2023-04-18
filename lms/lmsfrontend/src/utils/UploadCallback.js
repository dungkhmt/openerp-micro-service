import React from "react";
import {DropzoneArea} from "material-ui-dropzone";
import {Button} from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Grid from "@material-ui/core/Grid";
import CircularProgress from "@material-ui/core/CircularProgress";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

export default function UploadCallback(props) {
  const {
    buttonTitle,
    multipleFile = false,
    handleUpload = () => {},
    setFiles,
    waiting,
    open,
    setOpen,
  } = props;

  function handleClose() {
    setOpen(false);
  }

  function handleOpen() {
    setOpen(true);
  }

  return (
    <div>
      {waiting ? (
        <CircularProgress color={"secondary"} />
      ) : (
        <Button
          color={"primary"}
          variant={"contained"}
          startIcon={<CloudUploadIcon />}
          onClick={handleOpen}
        >
          {buttonTitle}
        </Button>
      )}

      <Dialog
        fullWidth={true}
        maxWidth={"sm"}
        open={open}
        onClose={handleClose}
      >
        <DialogTitle>{buttonTitle}</DialogTitle>
        <DialogContent>
          <Grid xs container>
            <DropzoneArea
              open={open}
              onChange={(files) => setFiles(files)}
              filesLimit={multipleFile ? 999 : 1}
              acceptedFiles={[""]}
              // acceptedFiles={["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]}
              maxFileSize={50000000}
              onClose={handleClose}
            />
          </Grid>

          <p>­</p>

          <Grid xs container>
            <Button variant="contained" color="primary" onClick={handleUpload}>
              Tải lên
            </Button>
          </Grid>
        </DialogContent>
      </Dialog>
    </div>
  );
}
