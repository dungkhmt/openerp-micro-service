/* eslint-disable */
import {Button, Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";

function StudentMyQuizTestList() {
  const params = useParams();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const history = useHistory();
  const [ListQuiz, setListQuizs] = useState([]);

  const onClickQuizId = (quizid, viewTypeId) => {
    console.log("click " + quizid);

    //history.push("/edu/class/student/quiztest/detail", {
    //  testId: quizid,
    //  viewTypeId: viewTypeId,
    //});

    history.push("/edu/class/student/quiztest/detail/" + quizid);
  };
  const columns = [
    {
      title: "Mã Quiz Test",
      field: "testId",
      render: (rowData) =>
        rowData["statusId"] == "STATUS_APPROVED" ? (
          <a
            style={{ cursor: "pointer" }}
            onClick={() => {
              onClickQuizId(rowData["testId"], rowData["viewTypeId"]);
            }}
          >
            {" "}
            {rowData["testId"]}{" "}
          </a>
        ) : (
          <p>{rowData["testId"]}</p>
        ),
    },
    { title: "Quiz Test Name", field: "testName" },
  ];

  async function getQuizList() {
    request(
      // token,
      // history,
      "get",
      "/get-my-quiz-test-list",
      (res) => {
        console.log(res);
        setListQuizs(res.data);
      },
      { 401: () => {} }
    );
  }

  useEffect(() => {
    getQuizList();
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable title={"Quiz Tests"} columns={columns} data={ListQuiz} />
      </CardContent>
    </Card>
  );
}

export default StudentMyQuizTestList;
