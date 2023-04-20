import {Box, Button, CircularProgress, Grid, MenuItem, TextField,} from "@material-ui/core";
import Typography from "@mui/material/Typography";
import * as React from "react";
import {useEffect, useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {successNoti, warningNoti} from "../../../utils/notification";
import {authPostMultiPart, request} from "../../../api";
import {useDispatch, useSelector} from "react-redux";

export default function CreateTestCase(props) {
  const history = useHistory();
  const [value, setValue] = useState(0);
  const [input, setInput] = useState("");
  const [result, setResult] = useState("");
  const { problemId } = useParams();
  const token = useSelector((state) => state.auth.token);
  const [description, setDescription] = useState();
  const [load, setLoad] = useState(false);
  const [checkTestcaseResult, setCheckTestcaseResult] = useState(false);
  const [point, setPoint] = useState(0);
  const [isPublic, setIsPublic] = useState("N");
  const [isProcessing, setIsProcessing] = useState(false);
  const [filename, setFilename] = useState("");
  const [uploadMode, setUploadMode] = useState("EXECUTE");

  const dispatch = useDispatch();
  const [uploadMessage, setUploadMessage] = useState("");
  //const token = useSelector((state) => state.auth.token);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

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

  useEffect(() => {
    console.log("problemId ", problemId);
    console.log("token ", token);
    /*
    request("GET", "/problem-details/" + problemId, (res) => {
      console.log("res ", res);
      setDescription(res.data.problemDescription);
      setSolution(res.data.solution);
    }).then();
    */
  }, []);

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

    authPostMultiPart(dispatch, token, "/upload-test-case", formData)
      .then((res) => {
        setIsProcessing(false);
        console.log("handleFormSubmit, res = ", res);
        setUploadMessage(res.message);
        //if (res.status == "TIME_OUT") {
        //  alert("Time Out!!!");
        //} else {
        //}
      })
      .catch((e) => {
        setIsProcessing(false);
        console.error(e);
        //alert("Time Out!!!");
      });
  };
  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }
  const onInputChange = (event) => {
    let name = event.target.value;
    setFilename(name);
  };

  return (
    <Box>
      <Typography variant={"h4"}>Create new Test case</Typography>
      <Box
        style={{
          width: "400px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
        }}
      >
        <TextField
          autoFocus
          required
          id="point"
          label="Point"
          placeholder="Point"
          onChange={(event) => {
            setPoint(event.target.value);
          }}
        ></TextField>
        <TextField
          autoFocus
          // required
          select
          id="Public TestCase"
          label="Public TestCase"
          onChange={(event) => {
            setIsPublic(event.target.value);
          }}
          value={isPublic}
          style={{ width: "140px" }}
        >
          <MenuItem key={"Y"} value={"Y"}>
            {"Y"}
          </MenuItem>
          <MenuItem key={"N"} value={"N"}>
            {"N"}
          </MenuItem>
        </TextField>
        <TextField
          autoFocus
          // required
          select
          id="Mode"
          label="Upload Mode"
          onChange={(event) => {
            setUploadMode(event.target.value);
          }}
          value={uploadMode}
          style={{ width: "140px" }}
        >
          <MenuItem key={"NOT_EXECUTE"} value={"NOT_EXECUTE"}>
            {"NOT_EXECUTE"}
          </MenuItem>
          <MenuItem key={"EXECUTE"} value={"EXECUTE"}>
            {"EXECUTE"}
          </MenuItem>
        </TextField>
      </Box>
      <br />

      <form onSubmit={handleFormSubmit}>
        <Grid container spacing={1} alignItems="flex-end">
          <Grid item xs={3}>
            <input
              type="file"
              id="selected-upload-file"
              onChange={onFileChange}
            />
          </Grid>
          Description
          <TextField
            fullWidth
            style={{
              marginTop: "10px",
              marginBottom: "24px",
            }}
            multiline
            maxRows={10}
            value={description}
            onChange={(event) => {
              setDescription(event.target.value);
            }}
          ></TextField>
          Result
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
          ></TextField>
          <Grid item xs={2}>
            <Button
              color="primary"
              variant="contained"
              type="submit"
              onChange={onInputChange}
              width="100%"
            >
              SUBMIT
            </Button>
            <h2> Status: {uploadMessage}</h2>
          </Grid>
          {isProcessing ? <CircularProgress /> : ""}
        </Grid>
      </form>
    </Box>
  );
}
