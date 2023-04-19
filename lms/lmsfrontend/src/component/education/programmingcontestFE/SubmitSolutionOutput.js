import React from "react";
import {useParams} from "react-router";
import {authPostMultiPart} from "../../../api";
import {Button, Grid} from "@material-ui/core";
import {useDispatch, useSelector} from "react-redux";

export default function SubmitSolutionOutput() {
  const dispatch = useDispatch();
  const params = useParams();
  const contestId = params.contestId;
  const problemId = params.problemId;
  const testCaseId = params.testCaseId;
  const [filename, setFilename] = React.useState("");
  const [isProcessing, setIsProcessing] = React.useState(false);
  const [score, setScore] = React.useState(0);
  const token = useSelector((state) => state.auth.token);

  function onFileChange(event) {
    setFilename(event.target.files[0]);
    console.log(event.target.files[0].name);
  }
  const onInputChange = (event) => {
    let name = event.target.value;
    setFilename(name);
  };

  const handleFormSubmit = (event) => {
    event.preventDefault();
    let body = {
      testCaseId: testCaseId,
      problemId: problemId,
      contestId: contestId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", filename);

    authPostMultiPart(dispatch, token, "/submit-solution-output", formData)
      .then((res) => {
        setIsProcessing(false);
        console.log("result submit = ", res);
        setScore(res.score);
      })
      .catch((e) => {
        setIsProcessing(false);
        console.error(e);
      });
  };

  return (
    <div>
      SubmitSolutionOutput {contestId}:{problemId}:{testCaseId}
      <form onSubmit={handleFormSubmit}>
        <Grid container spacing={1} alignItems="flex-end">
          <Grid item xs={2}>
            <Button
              color="primary"
              type="submit"
              onChange={onInputChange}
              width="100%"
            >
              UPLOAD
            </Button>
          </Grid>

          <input
            type="file"
            id="selected-upload-file"
            onChange={onFileChange}
          />
        </Grid>
      </form>
      Score: {score}
    </div>
  );
}
