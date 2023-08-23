import {Button, CircularProgress, Grid, MenuItem, TextField,} from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import * as React from "react";
import {useEffect, useState} from "react";
import Typography from "@mui/material/Typography";
import {useHistory, useParams} from "react-router-dom";
import {request} from "../../../api";
import {successNoti, warningNoti} from "../../../utils/notification";
import HustContainerCard from "component/common/HustContainerCard";

export default function EditTestCase(props) {
  const history = useHistory();

  const [value, setValue] = useState(0);
  const [input, setInput] = useState("");
  const [result, setResult] = useState("");
  const [screenHeight, setScreenHeight] = useState(
    (window.innerHeight - 300) / 2 + "px"
  );
  const {problemId, testCaseId} = useParams();
  const [description, setDescription] = useState("");
  const [solution, setSolution] = useState("");
  const [load, setLoad] = useState(false);
  const [checkTestcaseResult, setCheckTestcaseResult] = useState(false);
  const [point, setPoint] = useState(0);
  const [isPublic, setIsPublic] = useState("");

  const [isProcessing, setIsProcessing] = useState(false);
  const [filename, setFilename] = useState("");
  const [uploadMessage, setUploadMessage] = useState("");
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
      "/testcases/" + problemId + "/result",
      (res) => {
        console.log("res", res);
        setLoad(false);
        setResult(res.data.result);
        setCheckTestcaseResult(true);
      },
      {},
      body
    ).then();
  };

  useEffect(() => {
    console.log("EditTestCase useEffect start....");

    request("GET", "/testcases/" + testCaseId, (res) => {
      setDescription(
        //res.data.problemDescription != null ? res.data.problemDescription : " "
        res.data.description
      );
      setSolution(
        res.data.problemSolution != null ? res.data.problemSolution : " "
      );
      setInput(res.data.testCase != null ? res.data.testCase : " ");
      setResult(res.data.correctAns != null ? res.data.correctAns : " ");
      setPoint(res.data.point != null ? res.data.point : " ");
      setIsPublic(res.data.isPublic != null ? res.data.isPublic : " ");
    }).then(() => {
      console.log("problemId", problemId);
      console.log("testCaseId", testCaseId);
      console.log("isPublic: ", isPublic);
    });
  }, []);

  const handleFormSubmit = (event) => {
    event.preventDefault();
    setIsProcessing(true);
    setUploadMessage("");
    let body = {
      //testCaseId:testCaseId,
      problemId: problemId,
      point: point,
      isPublic: isPublic,
      description: description,
      correctAnswer: result,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));

    if (filename !== "") {
      formData.append("file", filename);

      request(
        "put",
        "/testcases/" + testCaseId + "/file-upload",
        (res) => {
          res = res.data;
          setIsProcessing(false);
          console.log("handleFormSubmit, res = ", res);
          setUploadMessage(res.message);
          //if (res.status == "TIME_OUT") {
          //  alert("Time Out!!!");
          //} else {
          //}
        },
        {
          onError: (e) => {
            setIsProcessing(false);
            console.error(e);
            //alert("Time Out!!!");
          },
        },
        formData,
        config
      );
    } else {
      // without file attached
      request(
        "post",
        "/testcases/" + testCaseId,
        (res) => {
          res = res.data;
          setIsProcessing(false);
          console.log("handleFormSubmit, res = ", res);
          setUploadMessage(res.message);
          //if (res.status == "TIME_OUT") {
          //  alert("Time Out!!!");
          //} else {
          //}
        },
        {
          onError: (e) => {
            setIsProcessing(false);
            console.error(e);
            //alert("Time Out!!!");
          },
        },
        formData,
        config
      );
    }
  };

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  const onInputChange = (event) => {
    let name = event.target.value;
    setFilename(name);
  };

  return (
    <div>
      <HustContainerCard title={'Edit Testcase'}>
        <Grid container spacing={2}>
          <Grid item xs={2}>
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
            />
          </Grid>
          <Grid item xs={3}>
            <TextField
              // required
              select
              id="Public TestCase"
              label="Public TestCase"
              placeholder="Public TestCase"
              style={{width: '150px'}}
              onChange={(event) => {
                setIsPublic(event.target.value);
              }}
              value={isPublic}
            >
              <MenuItem key={"Y"} value={"Y"}>
                {"Y"}
              </MenuItem>
              <MenuItem key={"N"} value={"N"}>
                {"N"}
              </MenuItem>
            </TextField>
            <br/>
          </Grid>
        </Grid>
        <form onSubmit={handleFormSubmit}>
          <Grid container spacing={1} alignItems="flex-end" mt={1}>
            <Grid item xs={12}>
              <Button color="primary" variant="outlined" component="label">
                <PublishIcon/> Upload testcase file
                <input hidden type="file" id="selected-upload-file" onChange={onFileChange}/>
              </Button>
            </Grid>
            <br></br>
            <Grid item xs={12}>
              <TextField
                label="Description"
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
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Solution Output"
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
              />
            </Grid>
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
              {!isProcessing && <h2> Status: {uploadMessage}</h2>}
            </Grid>
            {isProcessing ? <CircularProgress/> : ""}
          </Grid>
        </form>
      </HustContainerCard>
    </div>
  );
}
