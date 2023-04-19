import React, {useEffect, useState} from "react";
import {request} from "api";
import {Link} from "react-router-dom";
import {errorNoti} from "../../../utils/notification";
import StandardTable from "../../table/StandardTable";

export default function QuizTestListParticipatedByCurrentTeacher() {
  const [quizTestsParticipatedByCurrentTeacher, setQuizTestsParticipatedByCurrentTeacher] = useState([]);

  const columns = [
    { title: "Mã kỳ thi", field: "testId",
      render: test => (
        <Link to={{ pathname: `/edu/class/quiztest/detail/${test.testId}` }} >
          { test.testId }
        </Link>
      )
    },
    { title: "Vai trò", field: "roleId" },
    { title: "Trạng thái", field: "statusId" }
  ];

  useEffect(getQuizTestsParticipatedByCurrentTeacher, []);

  function getQuizTestsParticipatedByCurrentTeacher() {
    let successHandler = res => {
      setQuizTestsParticipatedByCurrentTeacher(res.data);
    };
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", "get-quiz-tests-of-user-login", successHandler, errorHandlers);
  }

  return (
    <StandardTable
      title="Danh sách kỳ thi đã tham gia"
      columns={columns}
      data={quizTestsParticipatedByCurrentTeacher}
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true
      }}
    />
  );
}
