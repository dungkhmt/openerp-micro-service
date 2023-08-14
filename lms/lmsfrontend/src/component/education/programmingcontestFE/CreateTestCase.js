import {Box, Button, Chip, CircularProgress, Grid, MenuItem, TextField} from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import HustContainerCard from "../../common/HustContainerCard";
import * as React from "react";
import {useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {successNoti, warningNoti} from "../../../utils/notification";
import {request} from "../../../api";

//TODO: improve this screen
// 1. Add sample file: When user choose upload -> open a modal with sample file to download
// 2. Show the output of the testcase after submitting
export default function CreateTestCase(props) {
  const history = useHistory();
  const [input, setInput] = useState("");
  const [result, setResult] = useState("");
  const {problemId} = useParams();
  const [description, setDescription] = useState("");
  const [load, setLoad] = useState(false);
  const [checkTestcaseResult, setCheckTestcaseResult] = useState(false);
  const [point, setPoint] = useState(1);
  const [isPublic, setIsPublic] = useState("N");
  const [isProcessing, setIsProcessing] = useState(false);
  const [filename, setFilename] = useState("");
  const [uploadMode, setUploadMode] = useState("EXECUTE");

  const [uploadMessage, setUploadMessage] = useState("");

  const getTestCaseResult = () => {
    console.log("get test case result");
    setLoad(true);
    let body = {
      testcase: input,
    };

    request(
      "POST",
      "/get-test-case-result/" + problemId,
      (res) => {
        console.log("res", res);
        setLoad(false);
        setResult(res.data.result);
        setCheckTestcaseResult(true);
        if (res.data.status != "ok") {
          warningNoti(res.data.status, false);
        }
      },
      {},
      body
    ).then();
  };

  const saveTestCase = () => {
    if (!checkTestcaseResult) {
      // setShowSubmitWarming(true);
      warningNoti("You must test your test case result before save", true);
      return;
    }

    let body = {
      input: input,
      result: result,
      point: point,
      isPublic: isPublic,
    };

    request(
      "POST",
      "/save-test-case/" + problemId,
      (res) => {
        console.log("res", res);
        // setShowSubmitSuccess(true);
        history.goBack();
        successNoti("Your test case is saved", true);
      },
      {},
      body
    ).then();
  };

  const handleFormSubmit = (event) => {
    event.preventDefault();
    setIsProcessing(true);
    setUploadMessage("");
    let body = {
      problemId: problemId,
      point: point,
      isPublic: isPublic,
      description: description,
      uploadMode: uploadMode,
      correctAnswer: result,
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
      "/upload-test-case",
      (res) => {
        res = res.data;
        setIsProcessing(false);
        setUploadMessage(res.message);
      },
      {
        onError: (e) => {
          setIsProcessing(false);
          setUploadMessage("Upload failed");
          console.error(e);
        },
      },
      formData,
      config
    );
  };

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  const onInputChange = (event) => {
    let name = event.target.value;
    setFilename(name);
  };

  return (
    <HustContainerCard title={'Create new Test case'}>
      <Button color="primary" variant="outlined" component="label">
        <PublishIcon/> Upload testcase file
        <input hidden type="file" id="selected-upload-file" onChange={onFileChange}/>
      </Button>
      {filename && (
        <Chip
          style={{marginLeft: "20px"}}
          color="success"
          variant="outlined"
          label={filename.name}
          onDelete={() => setFilename(undefined)}
        />
      )}
      <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          marginTop: "24px"
        }}
      >
        <TextField
          autoFocus
          required
          type="number"
          id="point"
          label="Point"
          placeholder="Point"
          value={point}
          onChange={(event) => {
            setPoint(event.target.value);
          }}
          sx={{width: "10%"}}
        />
        <TextField
          select
          id="public"
          label="Public"
          onChange={(event) => {
            setIsPublic(event.target.value);
          }}
          value={isPublic}
          sx={{width: "10%"}}
        >
          <MenuItem key={"Y"} value={"Y"}>
            {"Yes"}
          </MenuItem>
          <MenuItem key={"N"} value={"N"}>
            {"No"}
          </MenuItem>
        </TextField>
        <TextField
          autoFocus
          select
          id="Mode"
          label="Upload Mode"
          onChange={(event) => {
            setUploadMode(event.target.value);
          }}
          value={uploadMode}
          sx={{width: "15%"}}
        >
          <MenuItem key={"NOT_EXECUTE"} value={"NOT_EXECUTE"}>
            {"NOT_EXECUTE"}
          </MenuItem>
          <MenuItem key={"EXECUTE"} value={"EXECUTE"}>
            {"EXECUTE"}
          </MenuItem>
        </TextField>
        <TextField
          id="description"
          label="Description (optional)"
          placeholder="Description for the testcase (Optional)"
          onChange={(event) => {
            setDescription(event.target.value);
          }}
          sx={{width: "60%"}}
        />
      </Box>

      <form onSubmit={handleFormSubmit}>
        <Grid container spacing={2} mt={2}>
          {/* Result
            <TextField
              fullWidth
              style={{
                marginTop: "10px",
                marginBottom: "24px",
              }}
              multiline
              maxRows={10}
              value={result}
              onChange={(event) => {
                setResult(event.target.value);
              }}
            ></TextField> */}
          <Grid item xs={8}>
            <Button
              color="primary"
              variant="contained"
              type="submit"
              onChange={onInputChange}
              width="100%"
            >
              SUBMIT
            </Button>
            {isProcessing ? <CircularProgress/> : <h2> Status: {uploadMessage}</h2>}
          </Grid>
        </Grid>
      </form>
    </HustContainerCard>
  );
}
