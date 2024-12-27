import React, { useMemo } from "react";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  filePreview: {
    border: "solid gray 1px",
    borderRadius: "5px",
    height: "85vh",
    width: '100%',
    objectFit: 'contain'
  },
}));

export default function FilePreviewUrl(props) {
  const classes = useStyles();
  const file = useMemo(
    () => ({
      src: props.file
    }),
    [props.file]
  );
  return (
    <embed
      src={file.src}
      className={classes.filePreview}
      {...props}
    />
  );
}
