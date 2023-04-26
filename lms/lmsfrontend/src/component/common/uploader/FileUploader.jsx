import React, {useEffect, useState} from 'react';
import {Button, Chip} from "@mui/material";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import PropTypes from "prop-types";
import FilePreview from "./FilePreview";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  fileUploader: {},
  fileUploaderPreview: {
    display: "flex",
    alignItems: "flex-start",
    flexWrap: "wrap",
    columnGap: "10px",
    rowGap: "10px",
    margin: "10px 0"
  },
  fileUploaderChips: {
    display: "inline-flex",
    columnGap: "5px"
  }
}))

export default function FileUploader(props) {
  const classes = useStyles();
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => props.onChange(selectedFiles), [selectedFiles]);

  function updateSelectedFiles(newSelectedFiles) {
    let newSelectedFileArr = [...newSelectedFiles];
    for (let i = 0; i < newSelectedFileArr.length; i++) {
      let file = newSelectedFileArr[i];
      if (!file.type) {
        newSelectedFileArr[i] = file.slice(0, file.size, "text/plain")
      }
    }
    setSelectedFiles(newSelectedFileArr);
  }

  function deleteFileByIndex(index) {
    selectedFiles.splice(index, 1);
    setSelectedFiles([...selectedFiles]);
  }

  return (
    <div className={classes.fileUploader}>
      <div className={classes.fileUploaderPreview}>
        { props.preview &&
          selectedFiles.map((file, index) => (
            <FilePreview file={file} key={index} width="568" height="400"/>
          ))
        }
      </div>

      <Button component="label"
              variant="contained"
              startIcon={<UploadFileIcon />}
              sx={{ marginRight: "1rem" }}
              color="primary">
        Upload file
        <input type="file" hidden
               multiple={props.multiple}
               onChange={event => updateSelectedFiles(event.target.files)}/>
      </Button>

      <span className={classes.fileUploaderChips}>
        { selectedFiles &&
          selectedFiles.map((file, index) => (
            <Chip label={file.name} variant="outlined" color="primary" key={index}
                  onDelete={ props.clearable ? () => deleteFileByIndex(index) : null }/>
          ))
        }
      </span>
    </div>
  );
}

FileUploader.propTypes = {
  onChange: PropTypes.func.isRequired,
  multiple: PropTypes.bool,
  preview: PropTypes.bool,
  clearable: PropTypes.bool
}

FileUploader.defaultProps = {
  multiple: false,
  preview: true,
  clearable: true
}