import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";
import MaterialTable from "material-table";
import {Button, CircularProgress, TextField} from "@material-ui/core";

export default function CodeSimilarityCheck(props) {
  const contestId = props.contestId;
  const [codeSimilarity, setCodeSimilarity] = useState([]);
  const [threshold, setThreshold] = useState(50);
  const [isProcessing, setIsProcessing] = useState(false);
  const [userId, setUserId] = useState(null);
  const [problemId, setProblemId] = useState(null);
  const [clusters, setClusters] = useState([]);
  const [codeSimSummaryByParticipant, setCodeSimSummaryByParticipant] =
    useState([]);
  const columnCodeSimSummaryByParticipant = [
    { title: "UserID", field: "userId" },
    { title: "FullName", field: "fullname" },
    { title: "Highest Score", field: "highestSimilarity" },
  ];
  const columnCluster = [
    { title: "Problem", field: "problemId" },
    { title: "UserIds", field: "userIds" },
  ];

  const columns = [
    { title: "Source1", field: "sourceCode1" },
    { title: "user1", field: "userId1" },
    { title: "Submit Date 1", field: "date1" },
    { title: "Problem", field: "problemId" },
    { title: "Source2", field: "sourceCode2" },
    { title: "user2", field: "userId2" },
    { title: "Submit Date 2", field: "date2" },
    { title: "Score", field: "score" },
  ];

  function getCodeChecking() {
    let body = {
      contestId: contestId,
      threshold: threshold,
      userId: userId,
      problemId: problemId,
    };
    request(
      "post",
      //"/get-code-similarity/" + contestId,
      "/get-code-similarity",
      (res) => {
        console.log("getCodeChecking Plagiarism, res = ", res.data);
        let data = res.data.map((c) => ({
          ...c,
          date1: toFormattedDateTime(c.submitDate1),
          date2: toFormattedDateTime(c.submitDate2),
        }));
        //setCodeSimilarity(res.data.codeSimilarityElementList);
        console.log("map data = ", data);
        setCodeSimilarity(data);
      },
      {},
      body
    );

    request(
      "post",
      //"/get-code-similarity/" + contestId,
      "/get-code-similarity-cluster",
      (res) => {
        console.log("getCodeChecking Plagiarism, res = ", res.data);
        /*
        let data = res.data.map((c) => ({
          ...c,
          date1: toFormattedDateTime(c.submitDate1),
          date2: toFormattedDateTime(c.submitDate2),
        }));
        */
        //setCodeSimilarity(res.data.codeSimilarityElementList);
        console.log("map data = ", res.data);
        setClusters(res.data);
      },
      {},
      body
    );

    request(
      "get",
      //"/get-code-similarity/" + contestId,
      "get-code-similarity-summary-of-participants/" + contestId,
      (res) => {
        console.log("getCodeSimilaritySummaryOfParticipants, res = ", res.data);
        /*
        let data = res.data.map((c) => ({
          ...c,
          date1: toFormattedDateTime(c.submitDate1),
          date2: toFormattedDateTime(c.submitDate2),
        }));
        */
        //setCodeSimilarity(res.data.codeSimilarityElementList);
        //console.log("map data = ", res.data);
        setCodeSimSummaryByParticipant(res.data);
      }
    );
  }
  function computeSimilarity(event) {
    event.preventDefault();
    setIsProcessing(true);
    let body = {
      threshold: threshold,
    };
    request(
      "post",
      "/check-code-similarity/" + contestId,

      (res) => {
        console.log("getCodeChecking, res = ", res.data);
        let data = res.data.codeSimilarityElementList.map((c) => ({
          ...c,
          date1: toFormattedDateTime(c.submitDate1),
          date2: toFormattedDateTime(c.submitDate2),
        }));
        //setCodeSimilarity(res.data.codeSimilarityElementList);
        setIsProcessing(false);
        setCodeSimilarity(data);
      },
      {},
      body
    );
  }
  useEffect(() => {}, []);
  return (
    <div>
      <TextField
        autoFocus
        required
        id="Threshold"
        label="Threshold"
        placeholder="Threshold"
        value={threshold}
        onChange={(event) => {
          setThreshold(event.target.value);
        }}
      ></TextField>
      (%)
      <TextField
        autoFocus
        required
        id="userId"
        label="userId"
        placeholder="UserId"
        value={userId}
        onChange={(event) => {
          setUserId(event.target.value);
        }}
      ></TextField>
      <TextField
        autoFocus
        required
        id="problemId"
        label="problemId"
        placeholder="ProblemId"
        value={problemId}
        onChange={(event) => {
          setProblemId(event.target.value);
        }}
      ></TextField>
      <Button variant="contained" color="secondary" onClick={getCodeChecking}>
        View Code Similarity
      </Button>
      {isProcessing ? <CircularProgress /> : ""}
      <MaterialTable columns={columns} data={codeSimilarity}></MaterialTable>
      <MaterialTable columns={columnCluster} data={clusters}></MaterialTable>
      <MaterialTable
        columns={columnCodeSimSummaryByParticipant}
        data={codeSimSummaryByParticipant}
      ></MaterialTable>
    </div>
  );
}
