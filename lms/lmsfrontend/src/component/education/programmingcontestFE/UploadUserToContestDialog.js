import React, {useRef, useState} from "react";
import {request} from "../../../api";
import {errorNoti, successNoti} from "../../../utils/notification";

import {Box, Button, Card, CardContent, Chip, Typography} from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import SendIcon from "@mui/icons-material/Send";
import {LoadingButton} from "@mui/lab";
import HustModal from "../../common/HustModal";

export default function UploadUserToContestDialog(props) {
  const { isOpen, contestId } = props;
  const [isProcessing, setIsProcessing] = useState(false);
  const fileInputRef = useRef(null);
  const [filename, setFilename] = useState(null);

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  const downloadSampleFile = () => {
    window.location.href = '/static/excels/sample-upload-user_v2.xlsx';
  };

  const handleUpload = () => {
    setIsProcessing(true);
    let body = {
      contestId: contestId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", filename);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/contests/students/upload-list",
      (res) => {
        setIsProcessing(false);
      },
      {
        onError: (e) => {
          setIsProcessing(false);
        },
      },
      formData,
      config
    );
  }

  return (
    <HustModal
      open={isOpen}
      title={'Upload Users to Contest'}
      onOk={handleUpload}
      onClose={() => {}}
      textOk="Upload"
      textClose="Cancel"
    >
      <Box display="flex" justifyContent="flex-start" alignItems="center" width="100%" sx={{mb: 2}}>
        <input type="file" id="selected-upload-file" onChange={onFileChange} ref={fileInputRef}/>
        {filename && (
          <Chip
            color="success"
            variant="outlined"
            label={filename.name}
            onDelete={() => setFilename(undefined)}
          />
        )}

        <Button sx={{marginLeft: "24px"}} variant={"outlined"} onClick={downloadSampleFile}>Download Template</Button>
      </Box>
    </HustModal>
  );
}
