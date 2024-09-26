import React, {useState} from "react";
import {request} from "../../../api";
import {errorNoti, successNoti} from "../../../utils/notification";

import { Button, Card, CardContent, Chip, Typography } from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import SendIcon from "@mui/icons-material/Send";
import {LoadingButton} from "@mui/lab";

export default function ManagerSubmitCodeOfParticipant(props) {
  const { contestId, onClose } = props;
  const [filename, setFilename] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);
  function submitCode(event) {
    event.preventDefault();

    setIsProcessing(true);
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify({ contestId }));
    formData.append("file", filename);

    let successHandler = (res) => {
      setIsProcessing(false);
      setFilename(undefined);
      successNoti("Submit successfully", true);
    };
    let errorHandlers = {
      onError: (error) => {
        setIsProcessing(false);
        errorNoti("Exception when submit", true);
      },
    };
    request(
      "POST",
      "/teacher/submissions/participant-code",
      successHandler,
      errorHandlers,
      formData
    );

    onClose();
  }
  return (
    <div>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          columnGap: "10px",
          marginBottom: "10px",
        }}
      >
        <Button color="primary" variant="contained" component="label">
          <PublishIcon /> Select excel file to import
          <input
            type="file"
            hidden
            onChange={(event) => setFilename(event.target.files[0])}
          />
        </Button>
        <LoadingButton
          loading={isProcessing}
          endIcon={<SendIcon />}
          disabled={!filename}
          color="primary"
          variant="contained"
          onClick={submitCode}
        >
          Submit
        </LoadingButton>
      </div>
      {filename && (
        <Chip
        color="success"
        variant="outlined"
        label={filename.name}
        onDelete={() => setFilename(undefined)}
        />
        )}
      <div>File cần có định dạng {"<userID>_<problemcode>.<extension>"} Ví dụ: 20210000_AddNumber.cpp</div>
    </div>
  );
}
