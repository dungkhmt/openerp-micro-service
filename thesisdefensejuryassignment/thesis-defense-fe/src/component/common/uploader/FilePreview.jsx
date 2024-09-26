import React, {useMemo} from 'react';
import PropTypes from "prop-types";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  filePreview: {
    border: "solid gray 2px",
    borderRadius: "5px"
  }
}))

const FALLBACK_CONTENT_TYPE = "text/plain";

export default function FilePreview(props) {
  const classes = useStyles();
  const file = useMemo(() => ({
    src: URL.createObjectURL(props.file),
    type: props.file.type || FALLBACK_CONTENT_TYPE
  }), [props.file])

  return (
    <embed src={file.src} key={file.src} type={file.type}
           className={classes.filePreview} {...props}/>
  );
}

FilePreview.propTypes = {
  file: PropTypes.instanceOf(Blob).isRequired
}