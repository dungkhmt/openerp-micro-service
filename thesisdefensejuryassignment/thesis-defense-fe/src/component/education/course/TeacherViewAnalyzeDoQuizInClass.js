import React, { useEffect, useState } from "react";
import { request } from "../../../api";
import { toFormattedDateTime } from "../../../utils/dateutils";
import MaterialTable from "material-table";
//import { Button } from "@material-ui/core";
import {Button, CircularProgress } from '@mui/material';

export default function TeacherViewLogUserQuizList(props) {
  const { classId } = props;
  const [data, setData] = useState([]);
  const [isProcessing, setIsProcessing] = useState(false);
  const columns = [
    { title: "UserID", field: "userId" },
    { title: "FullName", field: "fullname" },
    { title: "numberSelect", field: "numberSelect" },
    { title: "numberCorrect", field: "numberCorrect" },
    { title: "numberCorrectFastest", field: "numberCorrectFastest" },
  ];
  function getData() {
    setIsProcessing(true);
    request("get", "/get-analyze-do-quiz-in-class/" + classId, (res) => {
      console.log("get data analyze do quiz in class, res = ", res);
      const tmp = res.data;
      const content = tmp.map((c) => ({
        ...c,
        date: toFormattedDateTime(c.date),
      }));
      setData(content);
      setIsProcessing(false);
    });
  }

  function computeStatistic() {
    setIsProcessing(true);
    let body = {
      classId: classId,
    };
    request(
      "post",
      "/analyze-do-quiz-test-in-class",
      (res) => {
        console.log("Compute Do Quiz In Class Statistics, res = ", res.data);
        setIsProcessing(false);
      },
      {},
      body
    );
  }
  useEffect(() => {
    getData();
  }, []);
  return (
    <>
      <Button variant="contained" color="primary" onClick={computeStatistic}>
        Statistic
      </Button>
      {isProcessing && <CircularProgress/>}
      <MaterialTable title={"Data"} columns={columns} data={data} />
    </>
  );
}
