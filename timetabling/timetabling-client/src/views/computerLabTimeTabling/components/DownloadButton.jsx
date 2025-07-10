import React, { useRef } from "react";
import Button from "@mui/material/Button";
import FileDownloadOutlinedIcon from '@mui/icons-material/FileDownloadOutlined';
const DownloadButton = ({ fileUrl, fileName, label }) => {
  const downloadLinkRef = useRef(null);

  const handleButtonClick = () => {
    downloadLinkRef.current.click();
  };

  return (
    <>
      <Button color="success" variant="outlined" onClick={handleButtonClick}>
        <FileDownloadOutlinedIcon/>
        {label || "Download"}
      </Button>
      <a
        ref={downloadLinkRef}
        href={fileUrl}
        download={fileName}
        style={{ display: "none" }}
      >
        {" "}
      </a>
    </>
  );
};

export default DownloadButton;
