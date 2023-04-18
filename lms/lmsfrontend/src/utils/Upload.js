import React, {useState} from "react";
import {DropzoneArea} from "material-ui-dropzone";
import {Button} from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Grid from "@material-ui/core/Grid";
import CircularProgress from "@material-ui/core/CircularProgress";
import {API_URL} from "../config/config";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

export default function Upload(props) {
  const {
    url,
    token,
    buttonTitle,
    dispatch,
    handleSaveCallback = () => {},
    fullUrl,
    multipleFile = false,
  } = props;

  const [open, setOpen] = useState(false);

  const [files, setFiles] = useState([]);

  const [waiting, setWaiting] = useState(false);

  function handleSave() {
    setOpen(false);
    setWaiting(true);
    let data = new FormData();
    if (multipleFile) {
      for (let i = 0; i < files.length; i++) {
        data.append(`files[]`, files[i]);
      }
    } else {
      data.append("file", files[0]);
    }
    Promise.all([uploadFile(url, data, fullUrl)])
      .then(([response]) => {
        setWaiting(false);
        handleSaveCallback(response);
      })
      .catch((error) => {
        console.log(error);
        alert("Upload thất bại: " + error);
      });
  }

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
            <Button variant="contained" color="primary" onClick={handleSave}>
              Tải lên
            </Button>
          </Grid>
        </DialogContent>
      </Dialog>
    </div>
  );

  function uploadFile(url, file, fullUrl) {
    let fields;
    fields = {
      method: "POST",
      headers: {
        "X-Auth-Token": token,
      },
      body: file,
    };
    if (fullUrl) {
      return fetch(fullUrl, fields).then((res) => res.json());
    } else {
      return fetch(`${API_URL}/${url}`, fields).then((res) => res.json());
    }
  }
}
