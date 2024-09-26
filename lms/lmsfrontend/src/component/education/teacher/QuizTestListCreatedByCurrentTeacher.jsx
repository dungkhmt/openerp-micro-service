import AddIcon from "@material-ui/icons/Add";
import {Button, Typography} from "@mui/material";
import {request} from "api";
import {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {defaultDateFormat, defaultTimeFormat} from "../../../utils/dateutils";
import {errorNoti} from "../../../utils/notification";
import StandardTable from "../../table/StandardTable";

const columns = [
  {
    field: "testId",
    title: "Mã kỳ thi",
    render: (test) => (
      <Link
        to={{ pathname: `/edu/class/quiztest/detail/${test.testId}` }}
        style={{ textDecoration: "none" }}
      >
        {test.testId}
      </Link>
    ),
  },
  { field: "testName", title: "Tên kỳ thi" },
  {
    field: "classId",
    title: "Mã lớp",
    render: (test) => (
      <Link
        to={{ pathname: `/edu/teacher/class/${test.classId}` }}
        style={{ textDecoration: "none" }}
      >
        {test.classCode}
      </Link>
    ),
  },
  { field: "testDate", title: "Ngày thi" },
  { field: "testTime", title: "Giờ thi" },
  {
    field: "duration",
    title: "Thời lượng",
    render: (test) => <Typography>{test.duration + " phút"}</Typography>,
  },
];

export default function QuizTestListCreatedByCurrentTeacher() {
  const history = useHistory();

  const [quizTestList, setQuizTestList] = useState([]);
  useEffect(getAllQuizTestByUser, []);

  function getAllQuizTestByUser() {
    request(
      "get",
      "/get-all-quiz-test-by-user",
      (res) => {
        const quizTestsWithoutClassDetail = res.data;

        request(
          "get",
          "/edu/class/list/teacher",
          (res2) => {
            const classesOfTeacher = res2.data;

            setQuizTestList(
              initClassForQuizTests(
                quizTestsWithoutClassDetail,
                classesOfTeacher
              )
            );
          },
          {
            onError: (e) => {
              console.error("QuizTestList error", e);
              errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", 3000);
            },
          }
        );
      },
      {
        onError: (e) => {
          console.error("QuizTestList error", e);
          errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", 3000);
        },
      }
    );
  }

  const CreateQuizTestButton = (
    <Button onClick={() => history.push("create-quiz-test")} variant="outlined">
      <AddIcon style={{ color: "white" }} fontSize="default" /> Thêm mới
    </Button>
  );

  const actions = [{ icon: () => CreateQuizTestButton, isFreeAction: true }];

  return (
    <StandardTable
      title="Danh sách kỳ thi đã tạo"
      columns={columns}
      data={quizTestList}
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true,
      }}
      actions={actions}
    />
  );
}

function initClassForQuizTests(quizTests, classes) {
  let quizTestsWithClassDetail = [];
  const mapClassById = buildMapClassById(classes);

  quizTests.forEach((test) => {
    let classOfTest = mapClassById.get(test.classId);
    if (classOfTest) {
      quizTestsWithClassDetail.push({
        testId: test.testId,
        testName: test.testName,
        testDate: defaultDateFormat(test.scheduleDatetime),
        testTime: defaultTimeFormat(test.scheduleDatetime),
        duration: test.duration,
        classCode: classOfTest.classCode,
        classId: classOfTest.id,
      });
    }
  });

  return quizTestsWithClassDetail;
}

function buildMapClassById(classes) {
  return new Map(classes.map((aClass) => [aClass.id, aClass]));
}
