import React, {useEffect, useState} from "react";
import MaterialTable from "material-table";
import {request} from "../../../../api";

export default function StudentLearningProgressDetailProgrammingSubmissionResultOnProblem(
  props
) {
  const studentId = props.studentId;
  const [contestSubmissions, setContestSubmissions] = useState([]);
  const columns = [
    { title: "contestSubmissionId", field: "contestSubmissionId" },
    { title: "problemId", field: "problemId" },
    { title: "contestId", field: "contestId" },
    { title: "userId", field: "userId" },
    { title: "testCasePass", field: "testCasePass" },
    { title: "sourceCodeLanguage", field: "sourceCodeLanguage" },
    { title: "point", field: "point" },
    { title: "status", field: "status" },
    { title: "createAt", field: "createAt" },
  ];
  function getContestSubmissionProgrammingResult() {
    request(
      // token, history,
      "get",
      "/contests/users/" + studentId + "/contest-result",
      (res) => {
        //let lst = [];
        //res.data.map((e) => {
        //  lst.push(e);s
        //});
        //console.log("getClassesOfUser, lst = ", lst);
        setContestSubmissions(res.data);
      }
    );
  }
  useEffect(() => {
    getContestSubmissionProgrammingResult();
  }, []);
  return (
    <div>
      <MaterialTable title="" columns={columns} data={contestSubmissions} />
    </div>
  );
}
