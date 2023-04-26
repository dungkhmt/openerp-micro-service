import {Box, Button, Typography} from "@mui/material";
import {makeStyles} from "@material-ui/core/styles";
import React from "react";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useTranslation} from "react-i18next";
import {getFileType, saveByteArray} from "./covert";

const useStyles = makeStyles((theme) => ({
  fileContainer: {
    marginTop: "12px",
  },
  fileWrapper: {
    position: "relative",
  },
  fileDownload: {
    display: "flex",
    flexDirection: "row",
    marginBottom: "16px",
    alignItems: "center",
  },
  fileName: {
    fontStyle: "italic",
    paddingRight: "12px",
  },
  downloadButton: {
    marginLeft: "12px",
  },
  imageQuiz: {
    maxWidth: "70%",
  },
  buttonClearImage: {
    position: "absolute",
    top: "12px",
    right: "12px",
    zIndex: 3,
    color: "red",
    width: 32,
    height: 32,
    cursor: "pointer",
  },
}));

function FileUploadZone(props) {
  const {file, removable, onRemove} = props;
  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common"]
  );

  const classes = useStyles();

  return (
    <div key={file.id} className={classes.fileContainer}>
      <div className={classes.fileWrapper}>
        {getFileType(file.fileName) === "img" && (
          <img
            src={`data:image/jpeg;base64,${file.content}`}
            alt={file.fileName}
            className={classes.imageQuiz}
          />
        )}
        {getFileType(file.fileName) === "pdf" && (
          <Box className={classes.fileDownload}>
            <Typography
              variant="subtitle2"
              className={classes.fileName}
            >
              {file.fileName}
            </Typography>
            <Button
              variant="contained"
              color="success"
              className={classes.downloadButton}
              onClick={() =>
                saveByteArray(
                  file.fileName,
                  file.content,
                  "pdf"
                )
              }
            >
              Download
            </Button>
          </Box>
        )}
        {getFileType(file.fileName) === "word" && (
          <Box className={classes.fileDownload}>
            <Typography
              variant="subtitle2"
              className={classes.fileName}
            >
              {file.fileName}
            </Typography>
            <Button
              variant="contained"
              color="success"
              className={classes.downloadButton}
              onClick={() =>
                saveByteArray(
                  file.fileName,
                  file.content,
                  "word"
                )
              }
            >
              Download
            </Button>
          </Box>
        )}
        {getFileType(file.fileName) === "txt" && (
          <Box className={classes.fileDownload}>
            <Typography
              variant="subtitle2"
              className={classes.fileName}
            >
              {file.fileName}
            </Typography>
            <Button
              variant="contained"
              color="success"
              className={classes.downloadButton}
              onClick={() =>
                saveByteArray(
                  file.fileName,
                  file.content,
                  "txt"
                )
              }
            >
              Download
            </Button>
          </Box>
        )}
        {removable && <HighlightOffIcon
          className={classes.buttonClearImage}
          onClick={onRemove}
        />}
      </div>
    </div>
  )
}

export default FileUploadZone;
