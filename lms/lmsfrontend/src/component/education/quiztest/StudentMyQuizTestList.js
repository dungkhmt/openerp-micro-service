/* eslint-disable */
import {Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import {LinearProgress} from "@mui/material";

function StudentMyQuizTestList() {
  const params = useParams();

  const history = useHistory();
  const [ListQuiz, setListQuizs] = useState([]);
  const [loading, setLoading] = useState(true);
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
    setLoading(true);
    request(
      // token,
      // history,
      "get",
      "/get-my-quiz-test-list",
      (res) => {
        console.log(res);
        setListQuizs(res.data);
        setLoading(false);
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
      {loading && <LinearProgress/>}
        <MaterialTable title={"Quiz Tests"} columns={columns} data={ListQuiz} />
      </CardContent>
    </Card>
  );
}

export default StudentMyQuizTestList;
