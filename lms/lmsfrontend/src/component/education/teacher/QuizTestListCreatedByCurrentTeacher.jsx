import AddIcon from "@material-ui/icons/Add";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {Link, useHistory} from "react-router-dom";
import {authGet} from "../../../api";
import {defaultDateFormat, defaultTimeFormat} from "../../../utils/dateutils";
import {errorNoti} from "../../../utils/notification";
import {Button, Typography} from "@mui/material";
import StandardTable from "../../table/StandardTable";

const columns = [
  { field: "testId", title: "Mã kỳ thi",
    render: test => (
      <Link to={{ pathname: `/edu/class/quiztest/detail/${test.testId}` }}
            style={{ textDecoration: "none" }} >
        {test.testId}
      </Link>
    )
  },
  { field: "testName",  title: "Tên kỳ thi" },
  { field: "classId", title: "Mã lớp",
    render: test => (
      <Link to={{ pathname: `/edu/teacher/class/${test.classId}` }}
            style={{ textDecoration: "none" }} >
        {test.classCode}
      </Link>
    )
  },
  { field: "testDate", title: "Ngày thi" },
  { field: "testTime", title: "Giờ thi" },
  { field: "duration", title: "Thời lượng",
    render: test => (
      <Typography>{test.duration + ' phút'}</Typography>
    )
  }
];

export default function QuizTestListCreatedByCurrentTeacher() {
  const history = useHistory();
  const dispatch = useDispatch();
  const token = useSelector(state => state.auth.token);

  const [quizTestList, setQuizTestList] = useState([]);
  useEffect(getAllQuizTestByUser, []);

  async function getAllQuizTestByUser() {
    try {
      // Using Promise.all to optimize multiple api call
      const [quizTestsWithoutClassDetail, classesOfTeacher] = await Promise.all([
          authGet(dispatch, token, "/get-all-quiz-test-by-user"),
          authGet(dispatch, token, "/edu/class/list/teacher")
      ])
      setQuizTestList(initClassForQuizTests(quizTestsWithoutClassDetail, classesOfTeacher));
    } catch (e) {
      console.error("QuizTestList error", e);
      errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", 3000);
    }
  }

  const CreateQuizTestButton = (
    <Button onClick={() => history.push("create-quiz-test") }
            variant="outlined">
      <AddIcon style={{ color: "white" }} fontSize="default" /> Thêm mới
    </Button>
  )

  const actions = [{ icon: () => CreateQuizTestButton, isFreeAction: true }]

  return (
    <StandardTable
      title="Danh sách kỳ thi đã tạo"
      columns={columns}
      data={quizTestList}
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true
      }}
      actions={actions}
    />
  );
}

function initClassForQuizTests(quizTests, classes) {
  let quizTestsWithClassDetail = [];
  const mapClassById = buildMapClassById(classes);

  quizTests.forEach(test => {
    let classOfTest = mapClassById.get(test.classId);
    if (classOfTest) {
      quizTestsWithClassDetail.push({
        testId: test.testId,
        testName: test.testName,
        testDate: defaultDateFormat(test.scheduleDatetime),
        testTime: defaultTimeFormat(test.scheduleDatetime),
        duration: test.duration,
        classCode: classOfTest.classCode,
        classId: classOfTest.id
      })
    }
  })

  return quizTestsWithClassDetail;
}

function buildMapClassById(classes) {
  return new Map(classes.map(aClass => [aClass.id, aClass]));
}
