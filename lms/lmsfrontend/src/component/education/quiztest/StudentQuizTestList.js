/* eslint-disable */
import {Button, Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";

function StudentQuizList() {
  const history = useHistory();
  const [ListQuiz, setListQuizs] = useState([]);

  async function onRegisterClick(quizid) {
    await registerQuiz(quizid);
    await getQuizList();
  }
  const onClickQuizId = (quizid, viewTypeId) => {
    console.log("click " + quizid);
    history.push("/edu/class/student/quiztest/detail", {
      testId: quizid,
      viewTypeId: viewTypeId,
    });
  };
  const columns = [
    {
      title: "Mã Quiz Test",
      field: "testId",
      //   render: rowData => (
      //   <Link to={"/edu/programming-contest-detail/" + rowData["testId"]}>
      //   {rowData["contestId"]}
      //   </Link>
      //   )
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
    { title: "Tên ", field: "testName" },
    { title: "Ngày", field: "scheduleDatetime" },
    {
      title: "Trạng thái",
      field: "statusId",
      render: (rowData) =>
        rowData["statusId"] == null ? (
          <Button
            variant="contained"
            color="primary"
            onClick={() => {
              onRegisterClick(rowData["testId"]);
            }}
          >
            Tham gia
          </Button>
        ) : rowData["statusId"] == "STATUS_REGISTERED" ? (
          <p>Chờ phê duyệt </p>
        ) : rowData["statusId"] == "STATUS_APPROVED" ? (
          <p>Đã phê duyệt </p>
        ) : (
          <p>Bị từ chối </p>
        ),
    },
  ];

  async function getQuizList() {
    request(
      // token,
      // history,
      "get",
      "/get-all-quiz-test-user",
      (res) => {
        console.log(res);
        setListQuizs(res.data);
      },
      { 401: () => {} }
    );
  }
  async function registerQuiz(quizId) {
    let datasend = { testQuizId: quizId };
    request(
      // token,
      // history,
      "post",
      "create-quiz-test-participation-register",
      (res) => {
        console.log(res);
      },
      { 401: () => {} },
      datasend
    );
    console.log(datasend);
  }

  useEffect(() => {
    getQuizList();
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable
          title={"Danh sách Contest"}
          columns={columns}
          data={ListQuiz}
        />
      </CardContent>
    </Card>
  );
}

export default StudentQuizList;
